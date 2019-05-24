package com.imall.common.utils;

public class AndroidDevice {
	protected String brand;
	protected String model;

	protected String sn;
	protected String imei;
	protected String androidId;
	protected String mac;
	protected String buildString;//6位随机数
	protected Integer apiLevel;//18 19 20 之类
	protected String version1;//4.5.1
	protected String version2;//4.5

    protected String phoneNumber;//有的时候有+86，有的时候没有？

    protected String cpu = "armeabi-v7a";
    protected String cpu2 = "armeabi";

	private AndroidDevice() {
		super();
	}
	
	public AndroidDevice(String brand, String model) {
		this();
		this.brand = brand;
		this.model = model;
	}
	
	public void init(){
		if(this.imei == null){
			this.generateInfo();
		}
	}
	
	/**
	 * 生成随机的信息
	 */
	public void generateInfo(){
		this.sn = DeviceUtils.generateRandomSN();
		this.imei = DeviceUtils.generateRandomIEMI();
		this.androidId = ObjectUtils.generateRandom16BitCode(16);
		this.mac = DeviceUtils.generateRandomMac();
		this.buildString = DeviceUtils.generateRandomAndroidBuildString();

		this.version1 = DeviceUtils.generateRandomAndroidVersion();
		this.version2 = this.version1.substring(0, 3);
		this.apiLevel = DeviceUtils.getAndroidAPILevel(this.version1);
	}
	
	public String getBrand() {
		this.init();
		return brand;
	}
	
	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		this.init();
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSn() {
		this.init();
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getImei() {
		this.init();
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getMac() {
		this.init();
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getBuildString() {
		this.init();
		return buildString;
	}

	public void setBuildString(String buildString) {
		this.buildString = buildString;
	}

	public Integer getApiLevel() {
		this.init();
		return apiLevel;
	}

	public void setApiLevel(Integer apiLevel) {
		this.apiLevel = apiLevel;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}
		if (obj == this){
			return true;
		}
		if(this.getModel() == null && ((AndroidDevice) obj).getModel() == null){
			return true;
		}else if(this.getModel() == null){
			return false;
		}
		if (this.getModel().equals(((AndroidDevice) obj).getModel())) {
			return true;
		} else {
			return false;
		}
	}
	
	/* 
	 * generate the hash code by model
	 */
	@Override
	public int hashCode() {
		if(this.getModel() == null){
			return Long.valueOf(0).hashCode();
		}
		return this.getModel().hashCode();
	}
	
	public String getBrandModel(){
		if(this.getBrand() == null && this.getModel() == null){
			return "unknown unknown";
		}
		if(this.getBrand() == null){
			return "unknown " + this.model;
		}
		if(this.getModel() == null){
			return this.brand + " unknown";
		}
		return this.getBrand() + " " + this.getModel();
	}

	public String getVersion1() {
		this.init();
		return version1;
	}

	public void setVersion1(String version1) {
		this.version1 = version1;
	}

	public String getVersion2() {
		this.init();
		return version2;
	}

	public void setVersion2(String version2) {
		this.version2 = version2;
	}

	public String getAndroidId() {
		return androidId;
	}

	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getCpu2() {
        return cpu2;
    }

    public void setCpu2(String cpu2) {
        this.cpu2 = cpu2;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
