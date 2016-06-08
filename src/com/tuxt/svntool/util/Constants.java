package com.tuxt.svntool.util;

import java.io.File;

public class Constants {

	public static final String PROJECT_PATH=
			new File(Constants.class.getResource("/").getPath()).getParentFile().getParent();
}
