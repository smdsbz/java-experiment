package doctor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class BrlbItem {
	
	public BrlbItem(String ghbh, String brmc, String ghrq, String hzlb) {
		setGhbh(ghbh);
		setBrmc(brmc);
		setGhrq(ghrq);
		setHzlb(hzlb);
	}
	
	private StringProperty ghbh = new SimpleStringProperty("");	// �Һű��
	public StringProperty ghbhProperty() { return ghbh; }
	public void setGhbh(String val) { ghbh.set(val); }
	public String getGhbh() { return ghbh.get(); }
	
	private StringProperty brmc = new SimpleStringProperty("");	// ��������
	public StringProperty brmcProperty() { return brmc; }
	public void setBrmc(String val) { brmc.set(val); }
	public String getBrmc() { return brmc.get(); }
	
	private StringProperty ghrq = new SimpleStringProperty("");	// �Һ�����ʱ��
	public StringProperty ghrqProperty() { return ghrq; }
	public void setGhrq(String val) { ghrq.set(val); }
	public String getGhrq() { return ghrq.get(); }
	
	private StringProperty hzlb = new SimpleStringProperty("");	// �������
	public StringProperty hzlbProperty() { return hzlb; }
	public void setHzlb(String val) { hzlb.set(val); }
	public String getHzlb() { return hzlb.get(); }

}
