package hospital;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

import hospital.service.AppointmentService;
import hospital.service.PatientService;
import hospital.service.ReservationService;

public class HospitalRunner {

	private Scanner scanner = new Scanner(System.in);
	private Connection conn;

	// 기능 분리
	private AppointmentService appointmentService;
	private ReservationService reservationService;
	private PatientService patientService;

	public HospitalRunner() {
		super();
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", "hospital", "test1234");

			// 기능 분리
			appointmentService = new AppointmentService(scanner, conn);
			patientService = new PatientService(scanner, conn);
			reservationService = new ReservationService(scanner, conn);
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	// 메인 메뉴
	public void mainMenu() {
		while (true) {
			System.out.println();
			System.out.println("=".repeat(60));
			System.out.println(" ".repeat(25) + "메인 메뉴");
			System.out.println("=".repeat(60));
			System.out.println("1. 환자 관리 | 2. 예약 관리 | 3. 진료 관리 | 4. Exit");
			System.out.print("> 메뉴 선택: ");
			String menuNo = scanner.nextLine();
			System.out.println();

			switch (menuNo) {
			case "1" -> patientService.submenu();
			case "2" -> reservationService.submenu();
			case "3" -> appointmentService.submenu();
			case "4" -> exit();
			}
		}
	}

	public void exit() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {

			}
		}
		System.out.println();
		System.out.println("[시스템 종료]");
		System.exit(0);
	}

	public static void main(String[] args) {
		HospitalRunner hospitalRunner = new HospitalRunner();
		hospitalRunner.mainMenu();
	}

}
