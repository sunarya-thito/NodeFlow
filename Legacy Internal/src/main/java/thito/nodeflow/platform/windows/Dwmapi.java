package thito.nodeflow.platform.windows;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;

import java.util.*;

@Deprecated
public interface Dwmapi extends StdCallLibrary {
	Dwmapi INSTANCE = Native.load("Dwmapi", Dwmapi.class, W32APIOptions.ASCII_OPTIONS);

	int DWM_BB_ENABLE = 0x00000001;

	boolean DwmEnableBlurBehindWindow(HWND hWnd, DWM_BLURBEHIND pBlurBehind);

	public static class DWM_BLURBEHIND extends Structure
	{
		public int dwFlags;
		public boolean fEnable;
		public IntByReference hRgnBlur;
		public boolean fTransitionOnMaximized;

		@Override
		protected List getFieldOrder()
		{
			return Arrays.asList("dwFlags", "fEnable", "hRgnBlur", "fTransitionOnMaximized");
		}
	}
}