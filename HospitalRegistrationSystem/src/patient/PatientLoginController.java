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
	
	@FXML private TextField user_textfield;		// �û���
	@FXML private TextField passwd_textfield;	// ������
	@FXML private Button submit_button;			// �ύ��ť
	@FXML private Button exit_button;			// �˳���ť
	
	private BrxxDao brxx_dao;	// ������Ϣ

	
	@FXML private void initialize() {
		// Runtime Environment
		try {
			brxx_dao = new BrxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
		} catch (SQLException e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, "�޷����������ݿ⣡");
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
			Alert alert = new Alert(AlertType.ERROR, "�޷����������ݿ⣡");
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
				Alert alert = new Alert(AlertType.ERROR, "δ�ҵ���Ҫ��Դ�ļ���");
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
				Alert alert = new Alert(AlertType.ERROR, "�������ݿ����");
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
			Alert alert = new Alert(AlertType.ERROR, "�û���������");
			alert.showAndWait();
		} else if (retcode.equals("wrong password")) {
			Alert alert = new Alert(AlertType.ERROR, "�������");
			alert.showAndWait();
		}
		return;
	}
	
	@FXML void onExitButtonAction(ActionEvent evt) {
		Platform.exit();
		return;
	}

}









