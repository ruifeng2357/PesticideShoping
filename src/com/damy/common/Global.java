package com.damy.common;


import com.damy.datatypes.STBuyCatalogInfo;
import com.damy.datatypes.STSaleCatalogInfo;
import com.damy.datatypes.STSaleRejectInfo;
import com.damy.datatypes.STStoreUsingInfo;
import android.app.Activity;
import android.app.AlertDialog;

public class Global {
	
	public static long 					Cur_UserId = 0;
	public static String 				Cur_UserName = "";
	public static String 				Cur_UserLoginId = "";
	public static long 					Cur_ShopId = 0;
	public static String 				Cur_ShopName = "";
	public static String 				Cur_UserRole = "";
	public static int                   Cur_Type = 0;
	
	public static int					Cur_AdminRole = 0;
	public static long					Cur_AdminRegionId = 0;
	
	public static int 					PAGE_SIZE = 10;
	
	public static STBuyCatalogInfo		BuyCatalog_SelectedItem = new STBuyCatalogInfo();
	public static boolean				BuyCatalog_isSelected = false;
	
	public static STStoreUsingInfo		StoreUsing_SelectItem = new STStoreUsingInfo();
	public static boolean				SotreUsing_isSelected = false;
	
	public static STSaleCatalogInfo		SaleCatalog_SelectItem = new STSaleCatalogInfo();
	public static boolean				SaleCatalog_isSelected = false;
	
	public static STSaleRejectInfo		SaleReject_SelectItem = new STSaleRejectInfo();
	public static boolean				SaleReject_isSelected = false;
}

