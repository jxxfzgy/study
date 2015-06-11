/**
 * Cobub Razor
 *
 * An open source analytics android sdk for mobile applications
 *
 * @package		Cobub Razor
 * @author		WBTECH Dev Team
 * @copyright	Copyright (c) 2011 - 2012, NanJing Western Bridge Co.,Ltd.
 * @license		http://www.cobub.com/products/cobub-razor/license
 * @link		http://www.cobub.com/products/cobub-razor/
 * @since		Version 0.1
 * @filesource
 */
package com.wbtech.ums.pojo;

/** 
 *  
 * @author duzhou.xu
 *
 */
public class SCell{
	public int MCC;//移动国家码 460
    public int MCCMNC;//MCC(3位)+MNC(2位)
    public int MNC;//移动网络码 01
    public int LAC;//LAC:location area code 位置区码 （移动通信系统中）,是为寻呼而设置的一个区域
    public int CID;//Customer IDentity 手机的平台版本
}
