/*
 * Copyright (C) 2022. Winterreisender
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX short identifier: Apache-2.0
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalAtomicApi::class)

package com.github.winterreisender.webviewko

import kotlinx.cinterop.*
import com.github.winterreisender.cwebview.*
import kotlin.concurrent.atomics.*


private typealias BindContext = Pair<WebviewKo,WebviewKo.(String?) -> Pair<String,Int>?>
private typealias DispatchContext = Pair<WebviewKo,WebviewKo.() ->Unit>


/**
 * The Kotlin/Native binding to webview
 *
 * @constructor create a webview or throws `Exception` if failed
 * @param debug enable debug mode for webview
 * @param libPath not supported in Kotlin/Native
 */

actual class WebviewKo actual constructor(debug: Int, libPath :String?) {
    private val w :webview_t = webview_create(debug, null) ?: throw Exception("Failed to create webview")

    // Garbage Collection List for bind and dispatch
    private val disposeList = AtomicReference(listOf<StableRef<Any>>())
    private fun addDispose(s: StableRef<Any>){
        disposeList.store(
            mutableListOf<StableRef<Any>>().apply {
                addAll(disposeList.load())
                if(!contains(s)){
                    add(s)
                }
            }
        )
    }
    protected fun finalize() {
        disposeList.load().forEach { it.dispose() }
        webview_destroy(w)
    }

    /**
     * Updates the title of the native window.
     *
     * Must be called from the UI thread.
     *
     * @param v the new title
     */
    actual fun title(v: String) = webview_set_title(w,v)


    /**
     * Navigates webview to the given URL
     *
     * URL may be a data URI, i.e. "data:text/text,...". It is often ok not to url-encode it properly, webview will re-encode it for you. Same as [navigate]
     *
     * @param v the URL or URI
     * */
    actual fun url(v: String) = navigate(v)

    /**
     * Navigates webview to the given URL
     *
     * URL may be a data URI, i.e. "data:text/text,...". It is often ok not to url-encode it properly, webview will re-encode it for you. Same as [url]
     *
     * @param url the URL or URI
     * */
    actual fun navigate(url: String) = webview_navigate(w,url)

    /**
     * Set webview HTML directly.
     *
     * @param v the HTML content
     */
    actual fun html(v: String) = webview_set_html(w,v)


    actual enum class WindowHint(v :Int) {
        None(WEBVIEW_HINT_NONE),
        Min(WEBVIEW_HINT_MIN),
        Max(WEBVIEW_HINT_MAX),
        Fixed(WEBVIEW_HINT_FIXED)
    }
    /**
     * Updates the size of the native window.
     *
     * Accepts a WEBVIEW_HINT
     *
     * @param hints can be one of [WindowHint]
     */
    actual fun size(width: Int, height: Int, hints: WindowHint) =
        webview_set_size(w, width, height, hints.ordinal)

    /**
     * Injects JS code at the initialization of the new page.
     *
     * Same as `initJS`. Every time the webview will open a new page - this initialization code will be executed. It is guaranteed that code is executed before `window.onload`.
     *
     * @param js the JS code
     */
    actual fun init(js: String) = webview_init(w,js)

    /**
     * Evaluates arbitrary JS code.
     *
     * Evaluation happens asynchronously, also the result of the expression is ignored. Use the `webview_bind` function if you want to receive notifications about the results of the evaluation.
     *
     * @param js the JS code
     */
    actual fun eval(js: String) = webview_eval(w,js)


    /**
     * Binds a Kotlin function callback so that it will appear under the given name as a global JS function.
     *
     * Callback receives a request string. Request string is a JSON array of all the arguments passed to the JS function. If you need binding a C function, see [WebviewKo.cBind]
     *
     * @param name the name of the global JS function
     * @param fn the callback function which receives the request parameter in JSON as input and return the response to JS in JSON.
     */
    actual fun bindRaw(name: String, fn: WebviewKo.(String?) -> Pair<String,Int>?) {
        val ctx = StableRef.create(BindContext(this, fn))
        addDispose(ctx)

        webview_bind(
            w,name,
            staticCFunction { seq,req,arg ->
                val (webviewKo,callback) = arg!!.asStableRef<BindContext>().get()
                val (response, status) = callback(webviewKo, req?.toKString()) ?: return@staticCFunction
                webview_return(webviewKo.w, seq?.toKString(), status, response)
            },
            ctx.asCPointer()
        )
    }

    /**
     * Should be used in [bind] to throw an exception in JS
     *
     * This exception will be caught by [bind] and trigger the `Promise.reject(reason)` in JS.
     *
     * @param reason the reason shown in JS.
     * @param json the JSON Exception object for JS. If it's not null, `reason` willed be covered
     */
    actual class JSRejectException actual constructor(reason: String?, json :String?) : Throwable(json ?: """ "$reason" """)

    /**
     * Binds a Kotlin callback so that it will appear under the given name as a global JS function.
     *
     * @param name the name of the global JS function
     * @param fn the callback function which receives the request parameter in JSON as input and return the response JSON. If you want to reject the `Promise`, throw [JSRejectException] in `fn`
     */
    actual fun bind(name :String, fn: WebviewKo.(String) -> String) {
        bindRaw(name) {
            runCatching { fn(it ?: "") }.fold(
                onSuccess = { Pair(it, 0) },
                onFailure =  {
                    when(it) {
                        is JSRejectException -> Pair(""" "${it.message}" """, 1)
                        else -> throw it
                    }
                }
            )
        }
    }

    /**
     * Removes a callback that was previously set by `webview_bind`.
     *
     * @param name the name of JS function used in `webview_bind`
     */
    actual fun unbind(name: String) = webview_unbind(w,name)

    /**
     * Posts a function to be executed on the main thread.
     *
     * It safely schedules the callback to be run on the main thread on the next main loop iteration.
     *
     * @param fn the function to be executed on the main thread.
     *
     */
    actual fun dispatch(fn: WebviewKo.() -> Unit) {
        val ctx = StableRef.create(DispatchContext(this,fn))
        addDispose(ctx)
        webview_dispatch(
            w,
            staticCFunction { w,arg ->
                val ctx = arg!!.asStableRef<DispatchContext>()
                val (webviewKo,callback) = ctx.get()
                callback(webviewKo)
            },
            ctx.asCPointer()
        )
    }

    /**
     * Runs the main loop until it's terminated. **After this function exits - you must destroy the webview**.
     *
     * This will block the thread.
     */
    actual fun start() = webview_run(w)

    /**
     * Stops the main loop.
     *
     * It is safe to call this function from another other background thread.
     *
     */
    actual fun terminate() = webview_terminate(w)

    /**
     * Destroy the webview and close the native window.
     *
     * You must destroy the webview after [start]
     *
     */
    actual fun destroy() = webview_destroy(w)

    /**
     * Runs the main loop until it's terminated and destroy the webview after that.
     *
     * This will block the thread. This is the same as calling [start] and [destroy] serially
     */
    actual fun show() {
        webview_run(w)
        webview_destroy(w)
    }

    /**
     * Return the C Pointer of the webview.
     *
     * @return the [CPointer], of the webview, aka [webview_t]
     *
     */
    fun getWebviewPointer() = w
}


//    inline fun <reified R : Any> bindEx(name :String, crossinline fn: WebviewKo.(String?) -> R) {
//        //val isError = 1
//        bindRaw(
//            name
//        ) { it ->
//            when (R::class) {
//                Result::class -> (fn(it) as Result<String>    ).fold({Pair(it,0)}, {Pair(""" "$it" """,1)})
//                String::class -> runCatching {fn(it) as String}.fold({Pair(it,0)}, {Pair(""" "$it" """,1)} )
//                Unit::class   -> fn(it).let { null }
//                Nothing::class-> runCatching{ fn(it) }.fold({error("Unexpected Behavior: fun (*)->Nothing runs successfully.")}, {Pair(""" "$it" """,1)})
//                Any::class-> runCatching {fn(it) as String}.fold({Pair(it,0)}, {Pair(""" "$it" """,1)} )
//                else -> throw IllegalArgumentException(R::class.simpleName)
//            }
//        }
//    }