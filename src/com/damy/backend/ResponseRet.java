package com.damy.backend;

public class ResponseRet {

	public static final int RET_SUCCESS = 0;
	public static final int RET_FAILURE = 100;
	public static final int RET_DUPLICATEUSERID = 101;
	public static final int RET_DUPLICATESTORENAME = 102;
	public static final int RET_NOUSER = 103;
	public static final int RET_TICKETNUMUSED = 104;
	public static final int RET_NO_IMAGE = 105;
	public static final int RET_CATALOGOVERFLOW = 106;
	public static final int RET_NOCATALOG = 107;
	public static final int RET_NOCUSTOMER = 108;
	public static final int RET_SALECATALOGOVERFLOW = 109;
	public static final int RET_NO_SALECATALOG = 110;
	public static final int RET_OVER_AVAILDATE = 111;
	public static final int RET_DUPLICATEUSERNAME = 112;
	public static final int RET_DUPLICATEREGISTERID = 113;
    public static final int RET_TRIAL_VERSION = -1;
	
	public static final int RET_DUPLICATE_LARGENUMBER = 200;
	public static final int RET_REMAIN_INSUFFICIENT = 201;
	
	public static final int RET_INTERNAL_EXCEPTION = 500;
	public static final int RET_JSON_EXCEPTION = 501;
}
