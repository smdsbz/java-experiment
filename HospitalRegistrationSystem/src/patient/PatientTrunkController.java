package patient;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.DoubleStringConverter;
import tabelmodels.BrxxDao;
import tabelmodels.GhxxDao;
import tabelmodels.HzxxDao;
import tabelmodels.KsxxDao;
import tabelmodels.KsysDao;
import tabelmodels.PyzsQueryableDao;


public class PatientTrunkController {

	// GUI Controls
	@FXML private ComboBox<String> ksmc_combobox;	// 科室名称
	@FXML private ComboBox<String> ysxm_combobox;	// 医生姓名
	@FXML private ComboBox<String> hzlb_combobox;	// 号种类别
	@FXML private ComboBox<String> hzmc_combobox;	// 号种名称
	@FXML private TextField jkje_textfield;			// 交款金额
	@FXML private TextField yjje_textfield;			// 应缴金额（只写）
	@FXML private TextField syje_textfield;			// 剩余金额（只写）
	@FXML private Button submit_button;				// 提交按钮
	
	// DAOs
	private KsxxDao ksxx_dao;	// 科室信息
	private KsysDao ksys_dao;	// 科室医生
	private BrxxDao brxx_dao;	// 病人信息
	private HzxxDao hzxx_dao;	// 号种信息
	private GhxxDao ghxx_dao;	// 挂号信息
	
//	private PatientSessionData sess_data;	// 会话数据

	private void pyzsComboBoxHandler(PyzsQueryableDao dao, ComboBox<String> combobox) {
		String pyzs = combobox.getEditor().getText();
		combobox.getItems().clear();
		// if textinput is cleared, hide dropdown and DON'T do query
		if (pyzs.isEmpty()) {
			combobox.hide();
			return;
		}
		try {
			dao.queryAllByPyzsStartWith(combobox.getEditor().getText(), 0);
			combobox.getItems().clear();
			while (dao.nextRowInQuery()) {
				combobox.getItems().add(dao.getNameFromQuery());
			}
			combobox.show();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML private void initialize() {

		/**** Runtime ****/
		try {
			ksxx_dao = new KsxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
			ksys_dao = new KsysDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
			brxx_dao = new BrxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
			hzxx_dao = new HzxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
			ghxx_dao = new GhxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/**** GUI static configurations ****/
		hzlb_combobox.getItems().addAll(Arrays.asList("专家号", "非专家号"));
		// 金额 can only be positive decimal with at most 2 points
		jkje_textfield.setTextFormatter(new TextFormatter<>(new DoubleStringConverter() {
			@Override public Double fromString(String s) {
				double hundred_val = 0.0;
				try {
					hundred_val = Double.parseDouble(s) * 100.0;
				} catch (NumberFormatException e) {
					return 0.0;
				}
				if (hundred_val < 0.0) {
					return 0.0;
				}
				if (hundred_val % 1.0 != 0.0) {	// NOTE Strict equal!
					hundred_val = ((double)(int)hundred_val) + 1.0;
				}
				Platform.runLater(() -> submit_button.requestFocus());
				return hundred_val / 100.0;
			}
		}));
		jkje_textfield.setText("0.00");

		/**** Completions with pyzs ****/
		ksmc_combobox.getEditor().setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override public void handle(KeyEvent evt) {
				if (evt.getCharacter().equals("\r")) {
					return;
				}
				// TODO Switch to new implementation framework.
				pyzsComboBoxHandler(ksxx_dao, ksmc_combobox);
			}
		});
		ysxm_combobox.getEditor().setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override public void handle(KeyEvent evt) {
				if (evt.getCharacter().equals("\r")) {
					return;
				}
				String pyzs = ysxm_combobox.getEditor().getText();
				if (pyzs.isBlank()) {
					ysxm_combobox.hide();
					return;
				}
				String ksmc = ksmc_combobox.getEditor().getText();
				try {
					if (ksmc == null || !ksxx_dao.hasKsmc(ksmc)) {
						Alert alert = new Alert(AlertType.WARNING, "无效的科室名称！");
						alert.show();
						ksmc_combobox.requestFocus();
						return;
					}
				} catch (IllegalStateException | SQLException e) {
					e.printStackTrace();
					Platform.exit();
					return;
				}
				Boolean sfzj = null;
				if (hzlb_combobox.getValue() == null) {
					Alert alert = new Alert(AlertType.WARNING, "无效的号种类别！");
					alert.show();
					hzlb_combobox.requestFocus();
					return;
				}
				if (hzlb_combobox.getValue().equals("专家号")) {
					sfzj = true;
				} else if (hzlb_combobox.getValue().equals("非专家号")) {
					sfzj = false;
				} else {
					Alert alert = new Alert(AlertType.WARNING, "无效的号种类别！");
					alert.show();
					hzlb_combobox.requestFocus();
					return;
				}
				LinkedList<String> ysxm = null;
				try {
					ysxm = ksys_dao.queryYsmcByPyzsStartWith(pyzs, ksmc, sfzj.booleanValue(), 0);
				} catch (IllegalStateException | SQLException e) {
					e.printStackTrace();
					Platform.exit();
					return;
				}
				ysxm_combobox.getItems().setAll(ysxm);
				ysxm_combobox.show();
			}
		});
		hzmc_combobox.getEditor().setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override public void handle(KeyEvent evt) {
				if (evt.getCharacter().equals("\r")) {
					return;
				}
				String pyzs = hzmc_combobox.getEditor().getText();
				if (pyzs.isBlank()) {
					hzmc_combobox.hide();
					return;
				}
				String ksmc = ksmc_combobox.getEditor().getText();
				try {
					if (ksmc == null || !ksxx_dao.hasKsmc(ksmc)) {
						Alert alert = new Alert(AlertType.WARNING, "无效的科室名称！");
						alert.show();
						ksmc_combobox.requestFocus();
						return;
					}
				} catch (IllegalStateException | SQLException e1) {
					e1.printStackTrace();
					Platform.exit();
					return;
				}
				String hzlb = hzlb_combobox.getValue();
				if (hzlb == null) {
					Alert alert = new Alert(AlertType.WARNING, "无效的号种类别！");
					alert.show();
					hzlb_combobox.requestFocus();
					return;
				}
				LinkedList<String> hzmc = null;
				try {
					hzmc = hzxx_dao.queryHzmcByPyzsStartWith(pyzs, ksmc, hzlb.equals("专家号"), 0);
				} catch (IllegalStateException | SQLException e) {
					e.printStackTrace();
					Platform.exit();
					return;
				}
				hzmc_combobox.getItems().setAll(hzmc);
				hzmc_combobox.show();
			}
		});

		/**** Focus management triggers ****/
		ksmc_combobox.focusedProperty().addListener((ov, old, neo) -> {
			if (!neo) {	// blurring out
//				try {
//					if (!ksxx_dao.hasKsmc(ksmc_combobox.getEditor().getText())) {
//						Alert alert = new Alert(AlertType.WARNING, "无效的科室名称！");
//						alert.show();
//						ksmc_combobox.requestFocus();
//					}
//				} catch (IllegalStateException | SQLException e) {
//					e.printStackTrace();
//					Platform.exit();
//					return;
//				}
			} else {
				/* pass */
			}
		});
		hzlb_combobox.focusedProperty().addListener((ov, old, neo) -> {
			if (!neo) {
				if (hzlb_combobox.getValue() == null) {
					Alert alert = new Alert(AlertType.WARNING, "无效的号种类别！");
					alert.show();
					hzlb_combobox.requestFocus();
				}
			}
		});
		ysxm_combobox.focusedProperty().addListener((ov, old, neo) -> {
			// ignore blurring out
			if (!neo) {
				return;
			}
			try {
				if (!ksxx_dao.hasKsmc(ksmc_combobox.getEditor().getText())) {
					Alert alert = new Alert(AlertType.WARNING, "无效的科室名称！");
					alert.show();
					ksmc_combobox.requestFocus();
					return;
				}
				if (hzlb_combobox.getValue() == null) {
					Alert alert = new Alert(AlertType.WARNING, "请选择号种类别！");
					alert.show();
					hzlb_combobox.requestFocus();
					return;
				}
			} catch (IllegalStateException | SQLException e) {
				e.printStackTrace();
				Platform.exit();
				return;
			}
		});
		hzmc_combobox.focusedProperty().addListener((ov, old, neo) -> {
			if (!neo) {
				return;
			}
			try {
				if (!ksxx_dao.hasKsmc(ksmc_combobox.getEditor().getText())) {
					Alert alert = new Alert(AlertType.WARNING, "无效的科室名称！");
					alert.show();
					ksmc_combobox.requestFocus();
					return;
				}
				if (hzlb_combobox.getValue() == null) {
					Alert alert = new Alert(AlertType.WARNING, "请选择号种类别！");
					alert.show();
					hzlb_combobox.requestFocus();
					return;
				}
				if (!ksys_dao.hasYsmc(ysxm_combobox.getEditor().getText())) {
					Alert alert = new Alert(AlertType.WARNING, "无效的医生姓名！");
					alert.show();
					ysxm_combobox.requestFocus();
					return;
				}
			} catch (IllegalStateException | SQLException e) {
				e.printStackTrace();
				Platform.exit();
				return;
			}
		});

		/**** GUI update triggers ****/
		ksmc_combobox.valueProperty().addListener((ov, old, neo) -> ysxm_combobox.setValue(null));
		hzlb_combobox.valueProperty().addListener((ov, old, neo) -> ysxm_combobox.setValue(null));
		ysxm_combobox.valueProperty().addListener((ov, old, neo) -> hzmc_combobox.setValue(null));
		// set 应缴金额 and 剩余金额 on 号种名称 / 交款金额 changed
	 	hzmc_combobox.valueProperty().addListener((ov, old, neo) -> onHzmcOrJkjeChanged());
	 	jkje_textfield.focusedProperty().addListener((ov, old, neo) -> { if (neo == false) onHzmcOrJkjeChanged(); });
	}
	
	private void onHzmcOrJkjeChanged() {
		String hzmc = hzmc_combobox.getValue();
		// if no 号种名称 available, clear calculated fields
		if (hzmc == null || hzmc.isEmpty()) {
			syje_textfield.clear();
			yjje_textfield.clear();
			return;
		}
		try {
			String hzbh = hzxx_dao.getHzbhByHzmc(hzmc);
			// clear calculated field on invalid 号种名称
			if (hzbh == null) {
				syje_textfield.clear();
				yjje_textfield.clear();
				return;
			}
			double ghfy = hzxx_dao.getGhfyByHzbh(hzbh);	// 挂号费用
			PatientSessionData brdata = (PatientSessionData)hzmc_combobox.getScene().getUserData();
			double ycje = brxx_dao.getYcjeByBrbh(brdata.brbh);	// 预存金额
			String jkje_str = jkje_textfield.getText();
			double jkje = 0.0;	// 交款金额
			if (!jkje_str.isBlank()) {
				jkje = Double.parseDouble(jkje_str);
			}
			double syje = ycje + jkje - ghfy;	// 剩余金额
			double yjje = syje > 0.0 ? 0.0 : -syje;	// 应缴金额
			syje_textfield.setText(String.format("%.2f", syje));
			yjje_textfield.setText(String.format("%.2f", yjje));
		} catch (IllegalStateException | SQLException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}

	@FXML private void onSubmitButtonAction(ActionEvent evt) {
		String ksmc = ksmc_combobox.getEditor().getText();
		String hzlb = hzlb_combobox.getValue();
		String ysxm = ysxm_combobox.getEditor().getText();
		String hzmc = hzmc_combobox.getEditor().getText();
		double jkje = 0.0;
		// legality validation
		try {
			if (!ksxx_dao.hasKsmc(ksmc)) {
				Alert alert = new Alert(AlertType.WARNING, "无效的科室名称！");
				alert.showAndWait();
				ksmc_combobox.requestFocus();
				return;
			}
		} catch (IllegalStateException | SQLException e1) {
			e1.printStackTrace();
			Platform.exit();
			return;
		}
		if (hzlb == null) {
			Alert alert = new Alert(AlertType.WARNING, "请选择号种类别！");
			alert.showAndWait();
			hzlb_combobox.requestFocus();
			return;
		}
		try {
			if (!ksys_dao.hasYsmc(ysxm)) {
				Alert alert = new Alert(AlertType.WARNING, "无效的医生姓名！");
				alert.showAndWait();
				ysxm_combobox.requestFocus();
				return;
			}
			if (!hzxx_dao.hasHzmc(hzmc)) {
				Alert alert = new Alert(AlertType.WARNING, "无效的号种名称！");
				alert.showAndWait();
				hzmc_combobox.requestFocus();
				return;
			}
		} catch (IllegalStateException | SQLException e1) {
			e1.printStackTrace();
			Platform.exit();
			return;
		}
		// NOTE If TextField's first format fails, will get empty string, causing exception.
		try {
			jkje = Double.parseDouble(jkje_textfield.getText());
		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.WARNING, "请输入交款金额！");
			alert.showAndWait();
			jkje_textfield.requestFocus();
			return;
		}
		// validation complete, display confirmation to user
		String msg = String.format("科室名称：%s\n号种类别：%s\n医生姓名：%s\n号种名称：%s\n交款金额：%.2f\n",
				ksmc, hzlb, ysxm, hzmc, jkje);
//		System.out.println(msg);
//		System.out.println("==========================");
		Alert dialog = new Alert(AlertType.CONFIRMATION, msg);
		dialog.setHeaderText("请确认您的挂号信息。");
		Optional<ButtonType> conf_result = dialog.showAndWait();
		if (conf_result.get() != ButtonType.OK) {
			return;
		}
		// use ID instead of names
		String ksbh = null;
		String ysbh = null;
		String hzbh = null;
		String brbh = ((PatientSessionData)ksmc_combobox.getScene().getUserData()).brbh;
		try {
			ksbh = ksxx_dao.getKsbhByKsmc(ksmc);
			ysbh = ksys_dao.getYsbhByYsmc(ysxm);
			hzbh = hzxx_dao.getHzbhByHzmc(hzmc);
		} catch (IllegalStateException | SQLException e) {
			e.printStackTrace();
			Platform.exit();
			return;
		}
		if (ksbh == null || ysbh == null || brbh == null || hzbh == null) {
			Alert alert = new Alert(AlertType.WARNING, "科室 / 医生 / 病人 / 号种不存在！");
			alert.showAndWait();
			return;
		}
		String ins_result = null;
		try {
			ins_result = ghxx_dao.insertRecord(hzxx_dao, hzbh, ksys_dao, ysbh, brxx_dao, brbh, jkje);
		} catch (IllegalStateException | SQLException e) {
			e.printStackTrace();
			Platform.exit();
			return;
		}
		if (!ins_result.equals("pass")) {
			Alert alert = new Alert(AlertType.WARNING, "挂号失败：" + ins_result);
			alert.showAndWait();
			return;
		}
		Alert alert = new Alert(AlertType.INFORMATION, "挂号成功！");
		alert.showAndWait();
		return;
	}

}










