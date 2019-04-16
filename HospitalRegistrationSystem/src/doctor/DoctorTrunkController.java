package doctor;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.LinkedList;
import java.util.SimpleTimeZone;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;
import tabelmodels.GhxxDao;


public class DoctorTrunkController {
	
	@FXML private TableView<BrlbItem> brlb_tabview;	// 病人列表
	@FXML private TableColumn<BrlbItem, String> ghbh_tabcol;	// 挂号编号列
	@FXML private TableColumn<BrlbItem, String> brmc_tabcol;	// 病人名称列
	@FXML private TableColumn<BrlbItem, String> ghrq_tabcol;	// 挂号日期列
	@FXML private TableColumn<BrlbItem, String> hzlb_tabcol;	// 号种类别列
	
	@FXML private Button handled_button;	// 设为已看病按钮
	
	@FXML private DatePicker start_datepicker;	// 起始查询时间
	@FXML private DatePicker end_datepicker;	// 结束查询时间
//	@FXML private Button query_button;	// 收入查询按钮
	
	@FXML private TableView<SrlbItem> srlb_tabview;	// 收入列表
	@FXML private TableColumn<SrlbItem, String> ksmc_tabcol;	// 科室名称列
	@FXML private TableColumn<SrlbItem, String> ysbh_tabcol;	// 医生编号列
	@FXML private TableColumn<SrlbItem, String> ysmc_tabcol;	// 医生名称列
	@FXML private TableColumn<SrlbItem, String> hzlb2_tabcol;	// 号种类别列
	@FXML private TableColumn<SrlbItem, String> ghrc_tabcol;	// 挂号人次列
	@FXML private TableColumn<SrlbItem, String> srhj_tabcol;	// 收入合计列

	@FXML private Button refresh_button;	// 刷新列表数据按钮
//	@FXML private Label refresh_time_label;	// 上次刷新时间
	
	private GhxxDao ghxx_dao = null;
	
	@FXML private void initialize() {
		/**** Runtime Steup ****/
		try {
			ghxx_dao = new GhxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
		} catch (SQLException e) {
			e.printStackTrace();
			// TODO Alert
			Platform.exit();
			return;
		}
		/**** GUI Setup ****/
		ghbh_tabcol.setCellValueFactory(new PropertyValueFactory<>("ghbh"));
		brmc_tabcol.setCellValueFactory(new PropertyValueFactory<>("brmc"));
		ghrq_tabcol.setCellValueFactory(new PropertyValueFactory<>("ghrq"));
		hzlb_tabcol.setCellValueFactory(new PropertyValueFactory<>("hzlb"));
		ksmc_tabcol.setCellValueFactory(new PropertyValueFactory<>("ksmc"));
		ysbh_tabcol.setCellValueFactory(new PropertyValueFactory<>("ysbh"));
		ysmc_tabcol.setCellValueFactory(new PropertyValueFactory<>("ysmc"));
		hzlb2_tabcol.setCellValueFactory(new PropertyValueFactory<>("hzlb"));
		ghrc_tabcol.setCellValueFactory(new PropertyValueFactory<>("ghrc"));
		srhj_tabcol.setCellValueFactory(new PropertyValueFactory<>("srhj"));
		/**** GUI Event Listeners ****/
		brlb_tabview.getSelectionModel().selectedItemProperty().addListener((ov, old, neo) -> {
			if (neo == null) {
				handled_button.setDisable(true);
			} else {
				handled_button.setDisable(false);
			}
		});
		start_datepicker.valueProperty().addListener((ov, old, neo) -> {
			if (neo == null || end_datepicker.getValue() == null) {
				return;
			}
			if (end_datepicker.getValue().isBefore(neo)) {
				Alert alert = new Alert(AlertType.WARNING, "结束时间必须在起始时间之后！");
				alert.show();
				return;
			}
		});
		end_datepicker.valueProperty().addListener((ov, old, neo) -> {
			if (neo == null || start_datepicker.getValue() == null) {
				return;
			}
			if (start_datepicker.getValue().isAfter(neo)) {
				Alert alert = new Alert(AlertType.WARNING, "结束时间必须在起始时间之后！");
				alert.show();
				return;
			}
		});
		/**** GUI Initialization ****/
		start_datepicker.setValue(LocalDate.now());
		end_datepicker.setValue(LocalDate.now());
		Platform.runLater(new Runnable() {
			@Override public void run() {
				refreshAllTables();
			}
		});
	}
	
	
	@FXML void onRefreshButtonAction(ActionEvent evt) {
		refreshAllTables();
	}
	
	@FXML void onHandledButtonAction(ActionEvent evt) {
		boolean opok = false;
		try {
			opok = ghxx_dao.setHandledByGhbh(brlb_tabview.getSelectionModel().getSelectedItem().getGhbh());
		} catch (IllegalStateException | SQLException e) {
			e.printStackTrace();
			Platform.exit();
			return;
		}
		if (!opok) {
			Platform.exit();
			return;
		}
		refreshAllTables();
	}
	
	void refreshAllTables() {
		refreshBrlb();
		refreshSrlb();
	}
	
	void refreshBrlb() {
		String ysbh = ((DoctorSessionData)brlb_tabview.getScene().getUserData()).ysbh;
		LinkedList<BrlbItem> brlb = null;
		try {
			brlb = ghxx_dao.queryBrlbItemByYsbh(ysbh);
		} catch (IllegalStateException | SQLException e) {
			e.printStackTrace();
			Platform.exit();
			return;
		}
		brlb_tabview.getItems().setAll(brlb);
		handled_button.setDisable(true);
	}
	
	@FXML private void onQueryButtonAction(ActionEvent evt) {
		refreshSrlb();
	}

	void refreshSrlb() {
		LinkedList<SrlbItem> srlb = null;
		if (start_datepicker.getValue() == null || end_datepicker.getValue() == null) {
			Alert alert = new Alert(AlertType.WARNING, "请选择查询时间段！");
			alert.show();
			return;
		}
		Timestamp start = Timestamp.from(start_datepicker.getValue().atStartOfDay(ZoneOffset.systemDefault()).toInstant());
		Timestamp end = Timestamp.from(end_datepicker.getValue().plusDays(1).atStartOfDay(ZoneOffset.systemDefault()).toInstant());
		System.out.println(start); System.out.println(end);
		if (end.before(start)) {
			Alert alert = new Alert(AlertType.WARNING, "结束时间必须在起始时间之后！");
			alert.show();
			return;
		}
		try {
			srlb = ghxx_dao.querySrlbItemInRange(start, end);
		} catch (IllegalStateException | SQLException e) {
			e.printStackTrace();
			Platform.exit();
			return;
		}
		srlb_tabview.getItems().setAll(srlb);
	}
	
}










