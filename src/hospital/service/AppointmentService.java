package hospital.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import hospital.dto.Appointment;

public class AppointmentService extends Service {

	public AppointmentService(Scanner scanner, Connection conn) {
		super(scanner, conn);
	}

	public void submenu() {
		while (true) {
			System.out.println();
			System.out.println("-".repeat(100));
			System.out.println(" ".repeat(45) + "진료 관리");
			System.out.println("-".repeat(100));
			System.out.println("1. 진료 내용 작성 | 2. 진료 내역 기간 조회 | 3. 당일 진료 가능 의사 보기 | 4. 메인메뉴로 돌아가기");
			System.out.print("> 메뉴 선택: ");
			String menuNo = scanner.nextLine();
			System.out.println();

			switch (menuNo) {
			case "1" -> createAppointment();
			case "2" -> getAllMidAppointment();
			case "3" -> getTodayAbleDoctor();
			case "4" -> {
				return;
			}
			}
		}
	}

	// 7.진료 내용 등록
	// 7-1. 처음부터 등록하기
	// 7-2. 예약에서 긁어오기
	public void createAppointment() {
		System.out.println();
		System.out.println("-".repeat(30) + "[ 진료 내용 작성 ]" + "-".repeat(30));
		System.out.println("1. 처음부터 등록하기 | 2. 금일 예약으로부터 작성하기 | 3. 진료 메뉴 ");
		System.out.print("> 메뉴 선택: ");
		String menuNo = scanner.nextLine();
		System.out.println();
		switch (menuNo) {
		case "1" -> initialRegistration();
		case "2" -> scrapByTodayReservation();
		}
	}

	// 7-1. 처음부터 등록하기
	public void initialRegistration() {
		Appointment appointment = new Appointment();
		System.out.println("[진료내역 처음부터 등록]");
		System.out.print("- 환자 id: ");
		appointment.setPatientId(Integer.parseInt(scanner.nextLine()));
		System.out.print("- 의사 id: ");
		appointment.setDoctorId(Integer.parseInt(scanner.nextLine()));
		System.out.print("- 작성날짜 (2025/04/30 형식): ");
		appointment.setAppointmentDate(scanner.nextLine());
		System.out.print("- 진료내역: ");
		appointment.setDescription(scanner.nextLine());

		// 보조메뉴출력
//		System.out.println("--------------------------------------------------");
//		System.out.println("위 내용으로 등록하실건가요? [1. Yes | 2. No]  > " );
//		String menuNo = scanner.nextLine();
//		
//		if(menuNo.equals("1")) {
		try {
			String sql = "INSERT INTO APPOINTMENT (APPOINTMENT_ID, PATIENT_ID, DOCTOR_ID, APPOINTMENT_DATE, DESCRIPTION) "
					+ "VALUES (APP_SEQ.NEXTVAL,?,?,to_date(?,'yyyy/mm/dd'),?)";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, appointment.getPatientId());
			pstmt.setInt(2, appointment.getDoctorId());
			pstmt.setString(3, appointment.getAppointmentDate());
			pstmt.setString(4, appointment.getDescription());

			// SQL문 실행
			int rows = pstmt.executeUpdate();

			if (rows > 0) {
				System.out.println("< 진료가 정상적으로 등록되었습니다 >");
			} else {
				System.out.println("< 진료 등록이 불가합니다 >");
			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
//		}

	}

	// 7-2. 금일 예약에서 긁어오기
	public void scrapByTodayReservation() {
		Appointment appointment = new Appointment();
		System.out.println("[금일 예약으로부터 진료 내용 작성]");
		// 예약 목록 조회
		ReservationService reservationService = new ReservationService(scanner, conn);
		reservationService.selectReservationToday();
		System.out.print("- 예약 id: ");
		String reservationId = scanner.nextLine();
		System.out.print("- 의사 id: ");
		appointment.setDoctorId(Integer.parseInt(scanner.nextLine()));
		System.out.print("- 진료내역: ");
		appointment.setDescription(scanner.nextLine());
		try {
			String getResSQL = "SELECT PATIENT_ID, TO_CHAR(VISIT_DATE, 'yyyy-mm-dd') AS visit_date "
					+ "FROM RESERVATION " + "WHERE RESERVATION_ID = ?";
			PreparedStatement pstmt2 = conn.prepareStatement(getResSQL);
			pstmt2.setString(1, reservationId);
			ResultSet rs = pstmt2.executeQuery();
			// 예약 ID가 존재하지 않으면 종료
			if (rs.next()) {
				appointment.setPatientId(rs.getInt("patient_id"));
				appointment.setAppointmentDate(rs.getString("visit_date"));
			} else {
				System.out.println("< 예약 ID가 존재하지 않습니다 >");
				return;
			}
			String sql = "INSERT INTO APPOINTMENT (APPOINTMENT_ID, PATIENT_ID, DOCTOR_ID, APPOINTMENT_DATE, DESCRIPTION) "
					+ "VALUES (APP_SEQ.NEXTVAL,?,?,to_date(?,'yyyy/mm/dd'),?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, appointment.getPatientId());
			pstmt.setInt(2, appointment.getDoctorId());
			pstmt.setString(3, appointment.getAppointmentDate());
			pstmt.setString(4, appointment.getDescription());
			// SQL문 실행
			int rows = pstmt.executeUpdate();
			if (rows > 0) {
				System.out.println("< 진료가 정상적으로 등록되었습니다 >");
			} else {
				System.out.println("< 진료 등록이 불가합니다 >");
			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void getAllMidAppointment() {
		System.out.println("-".repeat(20) + "[ 기간 예약 조회 ]" + "-".repeat(20));
		System.out.println("[ 조회할 날짜 구간 입력 ]");
		System.out.print("- 날짜 1 (2025/04/30 형식): ");
		String date1 = scanner.nextLine();
		System.out.print("- 날짜 2 (2025/04/30 형식): ");
		String date2 = scanner.nextLine();
		getAllAppointment(date1, date2);
	}

	// 8. 진료 내역 전체 조회 -> 기간별 진료내역 조회
	public void getAllAppointment(String date1, String date2) {
		System.out.println();
		System.out.println("-".repeat(40) + "[진료 내역 기간 조회]" + "-".repeat(40));
		try {
			String sql = "SELECT a.PATIENT_ID as p_id, p.name as p_name, p.sex as p_sex, a.doctor_id as d_id, d.name as d_name "
					+ "    , to_char(a.appointment_date,'yyyy-mm-dd') as app_date, a.description as description "
					+ "FROM appointment a JOIN patient p ON a.PATIENT_ID = p.PATIENT_ID "
					+ "    JOIN doctor d ON d.doctor_id = a.doctor_id "
					+ "WHERE a.appointment_date between TO_DATE(?,'yyyy/mm/dd') and TO_DATE(?,'yyyy/mm/dd')";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, date1);
			pstmt.setString(2, date2);

			ResultSet rs = pstmt.executeQuery();
			System.out.printf("%-6s | %-10s | %-4s | %-6s | %-10s | %-10s | %-20s\n", "환자ID", "환자명", "성별", "의사ID",
					"의사명", "예약일", "증상설명");
			// 구분선 출력
			System.out.println("-".repeat(100));
			while (rs.next()) {
				System.out.printf("%-8d | %-10s | %-4s | %-8s | %-10s | %-15s | %-20s\n", rs.getInt("p_id"),
						rs.getString("p_name"), rs.getString("p_sex"), rs.getString("d_id"), rs.getString("d_name"),
						rs.getString("app_date"), rs.getString("description"));
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	// 9. 당일 진료 가능한 의사 보기
	public void getTodayAbleDoctor() {
		try {
			String sql = "SELECT doc.doctor_id AS doc_id, doc.name AS doc_name, dept.name AS dept_name "
					+ "FROM doctor doc JOIN department dept ON dept.department_id = doc.department_id "
					+ "WHERE doc.holiday NOT LIKE '%'||to_char(sysdate, 'dy')||'%'";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			// 헤더 출력
			System.out.printf("%-6s | %-10s | %-10s\n", "번호", "이름", "과");
			System.out.println("------------------------------");

			while (rs.next()) {
				System.out.printf("%-6s | %-10s | %-10s\n", rs.getInt("doc_id"), rs.getString("doc_name"),
						rs.getString("dept_name"));
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
