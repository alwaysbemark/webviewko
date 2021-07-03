package webview;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;

import java.nio.IntBuffer;

/**
 * JNA Wrapper for library <b>webview</b><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public interface WebviewLibrary extends Library {
	public static final String JNA_LIBRARY_NAME = "webview";
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(WebviewLibrary.JNA_LIBRARY_NAME);
	public static final WebviewLibrary INSTANCE = (WebviewLibrary)Native.loadLibrary(WebviewLibrary.JNA_LIBRARY_NAME, WebviewLibrary.class);
	/** <i>native declaration : src_c/webview.h:59</i> */
	public interface webview_dispatch_fn_callback extends Callback {
		void apply(Pointer w, Pointer arg);
	};
	/** <i>native declaration : src_c/webview.h:100</i> */
	public interface webview_bind_fn_callback extends Callback {
		void apply(Pointer charPtr1, Pointer voidPtr1);
	};
	/**
	 * passed here.<br>
	 * Original signature : <code>webview_t webview_create(int, void*)</code><br>
	 * <i>native declaration : src_c/webview.h:43</i>
	 */
	Pointer webview_create(int debug, Pointer window);
	/**
	 * Destroys a webview and closes the native window.<br>
	 * Original signature : <code>void webview_destroy(webview_t)</code><br>
	 * <i>native declaration : src_c/webview.h:46</i>
	 */
	void webview_destroy(Pointer w);
	/**
	 * must destroy the webview.<br>
	 * Original signature : <code>void webview_run(webview_t)</code><br>
	 * <i>native declaration : src_c/webview.h:50</i>
	 */
	void webview_run(Pointer w);
	/**
	 * background thread.<br>
	 * Original signature : <code>void webview_terminate(webview_t)</code><br>
	 * <i>native declaration : src_c/webview.h:54</i>
	 */
	void webview_terminate(Pointer w);
	/**
	 * to call this function, unless you want to tweak the native window.<br>
	 * Original signature : <code>void webview_dispatch(webview_t, webview_dispatch_fn_callback*, void*)</code><br>
	 * <i>native declaration : src_c/webview.h:58</i>
	 */
	void webview_dispatch(Pointer w, webview_dispatch_fn_callback fn, Pointer arg);
	/**
	 * pointer, when using Win32 backend the pointer is HWND pointer.<br>
	 * Original signature : <code>void* webview_get_window(webview_t)</code><br>
	 * <i>native declaration : src_c/webview.h:64</i>
	 */
	Pointer webview_get_window(Pointer w);
	/**
	 * Updates the title of the native window. Must be called from the UI thread.<br>
	 * Original signature : <code>void webview_set_title(webview_t, const char*)</code><br>
	 * <i>native declaration : src_c/webview.h:67</i><br>
	 * @deprecated use the safer methods {@link #webview_set_title(Pointer, String)} and {@link #webview_set_title(Pointer, Pointer)} instead
	 */
	@Deprecated 
	void webview_set_title(Pointer w, Pointer title);
	/**
	 * Updates the title of the native window. Must be called from the UI thread.<br>
	 * Original signature : <code>void webview_set_title(webview_t, const char*)</code><br>
	 * <i>native declaration : src_c/webview.h:67</i>
	 */
	void webview_set_title(Pointer w, String title);
	/**
	 * TODO: implement x/y and describe possible flags.<br>
	 * Original signature : <code>void webview_set_bounds(webview_t, int, int, int, int, int)</code><br>
	 * <i>native declaration : src_c/webview.h:71</i>
	 */
	void webview_set_bounds(Pointer w, int x, int y, int width, int height, int flags);
	/**
	 * TODO: not implemented yet.<br>
	 * Original signature : <code>void webview_get_bounds(webview_t, int*, int*, int*, int*, int*)</code><br>
	 * <i>native declaration : src_c/webview.h:76</i><br>
	 * @deprecated use the safer methods {@link #webview_get_bounds(Pointer, IntBuffer, IntBuffer, IntBuffer, IntBuffer, IntBuffer)} and {@link #webview_get_bounds(Pointer, IntByReference, IntByReference, IntByReference, IntByReference, IntByReference)} instead
	 */
	@Deprecated 
	void webview_get_bounds(Pointer w, IntByReference x, IntByReference y, IntByReference width, IntByReference height, IntByReference flags);
	/**
	 * TODO: not implemented yet.<br>
	 * Original signature : <code>void webview_get_bounds(webview_t, int*, int*, int*, int*, int*)</code><br>
	 * <i>native declaration : src_c/webview.h:76</i>
	 */
	void webview_get_bounds(Pointer w, IntBuffer x, IntBuffer y, IntBuffer width, IntBuffer height, IntBuffer flags);
	/**
	 * properly, webview will re-encode it for you.<br>
	 * Original signature : <code>void webview_navigate(webview_t, const char*)</code><br>
	 * <i>native declaration : src_c/webview.h:82</i><br>
	 * @deprecated use the safer methods {@link #webview_navigate(Pointer, String)} and {@link #webview_navigate(Pointer, Pointer)} instead
	 */
	@Deprecated 
	void webview_navigate(Pointer w, Pointer url);
	/**
	 * properly, webview will re-encode it for you.<br>
	 * Original signature : <code>void webview_navigate(webview_t, const char*)</code><br>
	 * <i>native declaration : src_c/webview.h:82</i>
	 */
	void webview_navigate(Pointer w, String url);
	/**
	 * executed. It is guaranteed that code is executed before window.onload.<br>
	 * Original signature : <code>void webview_init(webview_t, const char*)</code><br>
	 * <i>native declaration : src_c/webview.h:87</i><br>
	 * @deprecated use the safer methods {@link #webview_init(Pointer, String)} and {@link #webview_init(Pointer, Pointer)} instead
	 */
	@Deprecated 
	void webview_init(Pointer w, Pointer js);
	/**
	 * executed. It is guaranteed that code is executed before window.onload.<br>
	 * Original signature : <code>void webview_init(webview_t, const char*)</code><br>
	 * <i>native declaration : src_c/webview.h:87</i>
	 */
	void webview_init(Pointer w, String js);
	/**
	 * receive notifications about the results of the evaluation.<br>
	 * Original signature : <code>void webview_eval(webview_t, const char*)</code><br>
	 * <i>native declaration : src_c/webview.h:92</i><br>
	 * @deprecated use the safer methods {@link #webview_eval(Pointer, String)} and {@link #webview_eval(Pointer, Pointer)} instead
	 */
	@Deprecated 
	void webview_eval(Pointer w, Pointer js);
	/**
	 * receive notifications about the results of the evaluation.<br>
	 * Original signature : <code>void webview_eval(webview_t, const char*)</code><br>
	 * <i>native declaration : src_c/webview.h:92</i>
	 */
	void webview_eval(Pointer w, String js);
	/**
	 * function.<br>
	 * Original signature : <code>void webview_bind(webview_t, const char*, webview_bind_fn_callback*, void*)</code><br>
	 * <i>native declaration : src_c/webview.h:99</i><br>
	 * @deprecated use the safer methods {@link #webview_bind(Pointer, String, webview_bind_fn_callback, Pointer)} and {@link #webview_bind(Pointer, Pointer, webview_bind_fn_callback, Pointer)} instead
	 */
	@Deprecated 
	void webview_bind(Pointer w, Pointer name, webview_bind_fn_callback fn, Pointer arg);
	/**
	 * function.<br>
	 * Original signature : <code>void webview_bind(webview_t, const char*, webview_bind_fn_callback*, void*)</code><br>
	 * <i>native declaration : src_c/webview.h:99</i>
	 */
	void webview_bind(Pointer w, String name, webview_bind_fn_callback fn, Pointer arg);
	/**
	 * If status is not zero - result is an error JSON object.<br>
	 * Original signature : <code>void webview_return(webview_t, const char*, int, const char*)</code><br>
	 * <i>native declaration : src_c/webview.h:106</i><br>
	 * @deprecated use the safer methods {@link #webview_return(Pointer, String, int, String)} and {@link #webview_return(Pointer, Pointer, int, Pointer)} instead
	 */
	@Deprecated 
	void webview_return(Pointer w, Pointer req, int status, Pointer result);
	/**
	 * If status is not zero - result is an error JSON object.<br>
	 * Original signature : <code>void webview_return(webview_t, const char*, int, const char*)</code><br>
	 * <i>native declaration : src_c/webview.h:106</i>
	 */
	void webview_return(Pointer w, String req, int status, String result);
}
