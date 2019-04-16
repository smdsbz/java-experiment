package doctor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SrlbItem {
	
	public SrlbItem(String ksmc, String ysbh, String ysmc, String hzlb, String ghrc, String srhj) {
		setKsmc(ksmc);
		setYsbh(ysbh);
		setYsmc(ysmc);
		setHzlb(hzlb);
		setGhrc(ghrc);
		setSrhj(srhj);
	}
	
	private StringProperty ksmc = new SimpleStringProperty("");	// 科室名称
	public StringProperty ksmcProperty() { return ksmc; }
	public void setKsmc(String val) { ksmc.set(val); }
	public String getKsmc() { return ksmc.get(); }
	
	private StringProperty ysbh = new SimpleStringProperty("");	// 医生编号
	public StringProperty ysbhProperty() { return ysbh; }
	public void setYsbh(String val) { ysbh.set(val); }
	public String getYsbh() { return ysbh.get(); }
	
	private StringProperty ysmc = new SimpleStringProperty("");	// 医生名称
	public StringProperty ysmcProperty() { return ysmc; }
	public void setYsmc(String val) { ysmc.set(val); }
	public String getYsmc() { return ysmc.get(); }
	
	private StringProperty hzlb = new SimpleStringProperty("");	// 号种类别
	public StringProperty hzlbProperty() { return hzlb; }
	public void setHzlb(String val) { hzlb.set(val); }
	public String getHzlb() { return hzlb.get(); }
	
	private StringProperty ghrc = new SimpleStringProperty("");	// 挂号人次
	public StringProperty ghrcProperty() { return ghrc; }
	public void setGhrc(String val) { ghrc.set(val); }
	public String getGhrc() { return ghrc.get(); }
	
	private StringProperty srhj = new SimpleStringProperty("");	// 收入合计
	public StringProperty srhjProperty() { return srhj; }
	public void setSrhj(String val) { srhj.set(val); }
	public String getSrhj() { return srhj.get(); }
	
}
