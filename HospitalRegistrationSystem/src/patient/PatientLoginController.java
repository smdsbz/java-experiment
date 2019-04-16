package patient;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import tabelmodels.BrxxDao;


public class PatientLoginController {
	
	@FXML private TextField user_textfield;		// 用户名
	@FXML private TextField passwd_textfield;	// 密码名
	@FXML private Button submit_button;			// 提交按钮
	@FXML private Button exit_button;			// 退出按钮
	
	private BrxxDao brxx_dao;	// 病人信息

	
	@FXML private void initialize() {
		// Runtime Environment
		try {
			brxx_dao = new BrxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
		} catch (SQLException e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, "无法连接至数据库！");
			alert.showAndWait();
			Platform.exit();
			return;
		}
	}
	
	@FXML private void onSubmitButtonAction(ActionEvent evt) {		
		String user = user_textfield.getText();
		String passwd = passwd_textfield.getText();
		String retcode = null;
		try {
			retcode = brxx_dao.verifyLogin(user, passwd);
		} catch (IllegalStateException | SQLException e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, "无法连接至数据库！");
			alert.showAndWait();
			Platform.exit();
			return;
		}
		if (retcode.equals("pass")) {
			// show trunk window
			Parent root = null;
			try {
				root = FXMLLoader.load(getClass().getResource("PatientTrunk.fxml"));
			} catch (IOException e) {
				e.printStackTrace();
				Alert alert = new Alert(AlertType.ERROR, "未找到必要资源文件！");
				alert.showAndWait();
				Platform.exit();
				return;
			}
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setResizable(false);
			// pass session data
			PatientSessionData sess = new PatientSessionData();
			sess.brmc = user;
			try {
				sess.brbh = brxx_dao.getBrbhByBrmc(sess.brmc);
			} catch (IllegalStateException | SQLException e) {
				e.printStackTrace();
				Alert alert = new Alert(AlertType.ERROR, "连接数据库出错！");
				alert.showAndWait();
				Platform.exit();
				return;
			}
			System.out.println(String.format("Logining in as %s (%s)!", sess.brmc, sess.brbh));
			stage.getScene().setUserData(sess);
//			// bind callback
//			stage.addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<WindowEvent>() {
//				@Override public void handle(WindowEvent evt) {
//					PatientSessionData sess = (PatientSessionData)((Window)evt.getSource()).getScene().getUserData();
//					System.out.println(String.format("Logining in as %s", sess.brmc));
//				}
//			});
			// switch stage
			stage.show();
			((Node)evt.getSource()).getScene().getWindow().hide();
		} else if (retcode.equals("user not found")) {
			Alert alert = new Alert(AlertType.ERROR, "用户名不存在");
			alert.showAndWait();
		} else if (retcode.equals("wrong password")) {
			Alert alert = new Alert(AlertType.ERROR, "密码错误");
			alert.showAndWait();
		}
		return;
	}
	
	@FXML void onExitButtonAction(ActionEvent evt) {
		Platform.exit();
		return;
	}

}









