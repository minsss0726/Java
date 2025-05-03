package hospital.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import hospital.dto.Department;
import hospital.dto.Patient;
import hospital.dto.Reservation;

public class ReservationService extends Service {

	public ReservationService(Scanner scanner, Connection conn) {
		super(scanner, conn);
	}

	public void submenu() {
		while (true) {
			System.out.println();
			System.out.println("-".repeat(90));
			System.out.println(" ".repeat(37) + "예약 관리");
			System.out.println("-".repeat(90));
			System.out.println("1. 예약 조회 | 2. 예약 생성 | 3. 예약 삭제 | 4. 예약 수정 | 5. 메인 메뉴");
			System.out.print("> 메뉴 선택: ");
			String menuNo = scanner.nextLine();
			System.out.println();

			switch (menuNo) {
			case "1" -> selectReservation();
			case "2" -> createReservation();
			case "3" -> deleteReservation();
			case "4" -> updateReservation();
			case "5" -> {
				return;
			}
			}
		}
	}

	// 예약 조회
	public void selectReservation() {
		// 4-1 OR 4-2
		System.out.println();
		System.out.println("-".repeat(20) + "[ 예약조회 ]" + "-".repeat(20));
		System.out.println("1. 금일 예약 조회 | 2. 기간 예약 조회 | 3. 예약 메뉴");
		System.out.print("> 메뉴 선택: ");
		String menuNo = scanner.nextLine();
		System.out.println();

		switch (menuNo) {
		case "1" -> selectReservationToday();
		case "2" -> selectReservationBetween();
		case "3" -> {
			return;
		}
		}

	}

	// 금일 예약 조회
	public void selectReservationToday() {
		System.out.println();
		System.out.println("-".repeat(30) + "[ 금일 예약자 명단 ]" + "-".repeat(30));
		try {
			String sql = ""
					+ "SELECT re.reservation_id as reservation_id, re.patient_id as patient_id, PA.NAME as pa_name, RE.VISIT_DATE as visit_date, DE.NAME as de_name, re.main_doctor_id AS md_id, doc.name as doc_name "
					+ " FROM RESERVATION re, DEPARTMENT de, PATIENT pa, DOCTOR doc"
					+ " WHERE re.department_id = de.department_id AND RE.PATIENT_ID = PA.PATIENT_ID AND VISIT_DATE = TO_CHAR(SYSDATE,'yyyy/mm/dd')"
					+ " AND doc.doctor_id(+) = re.main_doctor_id " + " ORDER BY reservation_id, PA.NAME";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			System.out.printf("%-4s | %-4s | %-4s | %-10s | %-8s | %-8s | %s \n", "예약번호", "환자번호", "환자이름", "방문일자",
					"담당의 번호", "담당의사명", "과");
			System.out.println("-".repeat(100));

			while (rs.next()) {
				Patient patient = new Patient();
				Department department = new Department();
				Reservation reservation = new Reservation();
				patient.setPatientId(rs.getInt("patient_id"));
				patient.setName(rs.getString("pa_name"));
				reservation.setReservationId(rs.getInt("reservation_id"));
				reservation.setMainDoctorId(rs.getInt("md_id"));
				reservation.setVisitDate(rs.getDate("visit_date").toString());
				department.setName(rs.getString("de_name"));

				System.out.printf("%-8d | %-8d | %-4s | %-15s | %-14d | %-10s | %s \n", reservation.getReservationId(),
						patient.getPatientId(), patient.getName(), reservation.getVisitDate().toString(),
						reservation.getMainDoctorId(), rs.getString("doc_name"), department.getName());
			}

			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 기간 예약 조회
	public void selectReservationBetween() {
		System.out.println("-".repeat(20) + "[ 기간 예약 조회 ]" + "-".repeat(20));
		System.out.println("[ 조회할 날짜 구간 입력 ]");
		System.out.print("- 날짜 1 (2025/04/30 형식): ");
		String date1 = scanner.nextLine();
		System.out.print("- 날짜 2 (2025/04/30 형식): ");
		String date2 = scanner.nextLine();
		selectReservationBetween(date1, date2);
	}

	public void selectReservationBetween(String date1, String date2) {
		try {
			String sql = ""
					+ "SELECT re.reservation_id as reservation_id, re.patient_id as patient_id, PA.NAME as pa_name, RE.VISIT_DATE as visit_date, DE.NAME as de_name, re.main_doctor_id AS md_id, doc.name as doc_name "
					+ " FROM RESERVATION RE, DEPARTMENT DE, PATIENT PA, DOCTOR doc"
					+ " WHERE re.department_id = de.department_id AND RE.PATIENT_ID = PA.PATIENT_ID AND re.visit_date between TO_DATE(?,'yyyy/mm/dd') and TO_DATE(?,'yyyy/mm/dd')"
					+ " AND doc.doctor_id(+) = re.main_doctor_id " + " ORDER BY re.visit_date, pa.name";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, date1);
			pstmt.setString(2, date2);

			ResultSet rs = pstmt.executeQuery();
			System.out.println();
			System.out.printf("%-4s | %-4s | %-4s | %-10s | %-8s | %-8s | %s \n", "예약번호", "환자번호", "환자이름", "방문일자",
					"담당의 번호", "담당의사명", "과");
			System.out.println("-".repeat(100));
			while (rs.next()) {
				Patient patient = new Patient();
				Department department = new Department();
				Reservation reservation = new Reservation();
				patient.setPatientId(rs.getInt("patient_id"));
				patient.setName(rs.getString("pa_name"));
				reservation.setReservationId(rs.getInt("reservation_id"));
				reservation.setMainDoctorId(rs.getInt("md_id"));
				reservation.setVisitDate(rs.getDate("visit_date").toString());
				department.setName(rs.getString("de_name"));

				System.out.printf("%-8d | %-8d | %-4s | %-15s | %-14d | %-10s | %s \n", reservation.getReservationId(),
						patient.getPatientId(), patient.getName(), reservation.getVisitDate().toString(),
						reservation.getMainDoctorId(), rs.getString("doc_name"), department.getName());
			}

			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 예약 생성
	public void createReservation() {

		try {
			String sql = "" + "INSERT INTO RESERVATION (RESERVATION_ID,PATIENT_ID,VISIT_DATE,DEPARTMENT_ID)"
					+ " VALUES (RES_SEQ.NEXTVAL,?,TO_date(?,'yyyy/mm/dd'),?)";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			System.out.println("[ 예약 등록 ]");
			System.out.print("- 환자 번호 : ");
			pstmt.setInt(1, Integer.parseInt(scanner.nextLine()));
			System.out.print("- 예약 날짜 (2025/04/30 형식) : ");
			String date = scanner.nextLine();
			pstmt.setString(2, date);

			String sqlDept = "SELECT dept.department_id AS dept_id, dept.name AS dept_name" + " FROM department dept";
			PreparedStatement pstmt2 = conn.prepareStatement(sqlDept);
			ResultSet rs = pstmt2.executeQuery();

			System.out.println();
			System.out.println("\t< 과 목록 >");
			System.out.printf("\t%-4s | %-4s \n", "과 번호", "과명");
			while (rs.next()) {
				System.out.printf("\t%-8d | %-4s \n", rs.getInt("dept_id"), rs.getString("dept_name"));
			}
			pstmt2.close();
			System.out.print("- 과 번호 : ");
			pstmt.setInt(3, Integer.parseInt(scanner.nextLine()));

			int rows = pstmt.executeUpdate();
			pstmt.close();
			System.out.println();
			if (rows > 0)
				System.out.println("< 예약을 등록헀습니다 >");
			else
				System.out.println("< 예약 등록이 불가합니다 >");

			System.out.print("\n+ 담당의를 지정하시겠습니까? (1. 예 2. 아니오) : ");
			String assign = scanner.nextLine();
			if (assign.equals("1")) {
				System.out.println("[ 담당의 배정 ]");
				String sql2 = "SELECT RES_SEQ.CURRVAL AS num FROM DUAL";
				PreparedStatement pstmtAssign = conn.prepareStatement(sql2);
				ResultSet rSet = pstmtAssign.executeQuery();
				if (rSet.next())
					assignDoctorToReservation(rSet.getInt("num"));

				pstmtAssign.close();
				rSet.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 예약 삭제
	public void deleteReservation() {
		try {
			String sql = "" + "DELETE FROM RESERVATION" + " WHERE RESERVATION_ID = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			System.out.println("[ 예약 삭제 ]");

			selectReservationBetween("1900/01/01", "2100/01/01");

			System.out.print("- 예약 번호 : ");
			pstmt.setInt(1, Integer.parseInt(scanner.nextLine()));

			int rows = pstmt.executeUpdate();
			pstmt.close();
			if (rows > 0)
				System.out.println("< 예약을 삭제헀습니다 >");
			else
				System.out.println("< 예약 삭제가 불가합니다 >");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 예약 수정
	private void updateReservation() {
		System.out.println();
		System.out.println("-".repeat(20) + "[ 예약 수정 ]" + "-".repeat(20));
		System.out.println("1. 예약 날짜 변경 | 2. 담당의 지정 ");
		System.out.print("> 메뉴 선택: ");
		String menuNo = scanner.nextLine();
		System.out.println();

		switch (menuNo) {
		case "1" -> updateReservationDate();
		case "2" -> {
			// 예약
			assignDoctorToReservation();
		}
		case "3" -> {
			return;
		}
		}
	}

	// 예약 날짜 변경
	private void updateReservationDate() {
		try {
			String sql = "" + "UPDATE RESERVATION SET VISIT_DATE = TO_DATE(?,'YYYY/MM/DD')"
					+ " WHERE RESERVATION_ID = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			System.out.println("[ 예약 날짜 변경 ]");
			selectReservationBetween("1900/01/01", "2100/01/01");
			System.out.print("- 예약 번호 : ");
			pstmt.setInt(2, Integer.parseInt(scanner.nextLine()));
			System.out.print("- 예약 날짜 (2025/04/30 형식) : ");
			String date = scanner.nextLine();
			pstmt.setString(1, date);
			int rows = pstmt.executeUpdate();
			pstmt.close();
			if (rows > 0)
				System.out.println("< 예약 날짜를 변경했습니다 >");
			else
				System.out.println("< 예약 수정이 불가합니다 >");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 담당의 배정
	private void assignDoctorToReservation() {
		System.out.println("[ 담당의 배정 ]");
		System.out.print("- 예약 번호 : ");
		int reservation_id = Integer.parseInt(scanner.nextLine());

		assignDoctorToReservation(reservation_id);

	}

	private void assignDoctorToReservation(int reservation_id) {
		try {
			String sql = "SELECT doc.doctor_id as doc_id, doc.name as doc_name "
					+ "FROM reservation re JOIN doctor doc on doc.department_id = re.department_id "
					+ "WHERE doc.holiday NOT LIKE '%' || TO_CHAR(re.visit_date, 'dy') || '%' "
					+ "AND re.reservation_id = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, reservation_id);

			ResultSet rs = pstmt.executeQuery();

			System.out.println("\t[ 해당 날 출근 가능 의사 목록 ]");
			System.out.printf("\t%-6s | %-10s \n", "번호", "의사명");
			System.out.println("\t------------------------------");
			while (rs.next()) {
				System.out.printf("\t%-6s | %-10s \n", rs.getInt("doc_id"), rs.getString("doc_name"));
			}
			rs.close();
			pstmt.close();

			System.out.print("- 지정하고 싶은 담당의 번호: ");
			int maindoctorId = Integer.parseInt(scanner.nextLine());
			String sql2 = "UPDATE reservation SET main_doctor_id = ? " + "WHERE reservation_id = ?";
			PreparedStatement pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setInt(1, maindoctorId);
			pstmt2.setInt(2, reservation_id);

			int rows = pstmt2.executeUpdate();
			if (rows > 0)
				System.out.println("< 담당의를 지정했습니다 >");
			else
				System.out.println("< 담당의 지정에 실패했습니다 >");
			pstmt2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
