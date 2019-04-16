package doctor;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import tabelmodels.KsysDao;


public class DoctorLoginController {
	
	@FXML private TextField user_textfield;	// 用户名
	@FXML private TextField pass_field;		// 密码
	@FXML private Button submit;	// 登陆按钮
	@FXML private Button quit;		// 退出按钮
	
	private KsysDao ksys_dao = null;	// 医生信息
	
	
	@FXML private void initialize() {
		try {
			ksys_dao = new KsysDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
		} catch (SQLException e) {
			// TODO Alert box
			Platform.exit();
			return;
		}
	}
	
	@FXML private void onQuitButtonAction(ActionEvent evt) {
		Platform.exit();
	}
	
	@FXML private void onSubmitButtonAction(ActionEvent evt) {
		String user = user_textfield.getText();
		String pass = pass_field.getText();
		try {
			String retcode = ksys_dao.verifyLogin(user, pass);
			if (retcode.equals("user not found")) {
				Alert alert = new Alert(AlertType.WARNING, "用户名不存在！");
				alert.showAndWait();
				return;
			}
			if (retcode.equals("wrong password")) {
				Alert alert = new Alert(AlertType.WARNING, "密码错误！");
				alert.showAndWait();
				return;
			}
			if (retcode.equals("pass")) {
				String ysbh = ksys_dao.getYsbhByYsmc(user);
				// TODO Stage transfer
				// load stage resources
				Parent root = null;
				try {
					root = FXMLLoader.load(getClass().getResource("DoctorTrunk.fxml"));
				} catch (IOException e) {
					e.printStackTrace();
					Platform.exit();
					return;
				}
				Stage stage = new Stage();
				stage.setScene(new Scene(root));
				stage.setResizable(false);
				// bind session data
				DoctorSessionData sess = new DoctorSessionData();
				sess.ysmc = user;
				sess.ysbh = ksys_dao.getYsbhByYsmc(user);
				stage.getScene().setUserData(sess);
				// swtich stage
				System.out.println(String.format("Logining in as %s (%s)", sess.ysmc, sess.ysbh));
				stage.show();
				((Node)evt.getSource()).getScene().getWindow().hide();
				return;
			}
			Platform.exit();
		} catch (IllegalStateException | SQLException e) {
			Platform.exit();
		}
	}

}











