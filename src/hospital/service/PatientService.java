package hospital.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import hospital.dto.Department;
import hospital.dto.Patient;
import hospital.dto.Reservation;


public class PatientService extends Service {

	public PatientService(Scanner scanner, Connection conn) {
		super(scanner, conn);
	}

	public void submenu() {
		while (true) {
			System.out.println();
			System.out.println("-".repeat(55));
			System.out.println(" ".repeat(22) + "환자 관리");
			System.out.println("-".repeat(55));
			System.out.println("1. 환자 등록 | 2. 환자 조회 | 3. 메인메뉴로 돌아가기 ");
			System.out.print("> 메뉴 선택: ");
			String menuNo = scanner.nextLine();
			System.out.println();

			switch (menuNo) {
			case "1" -> registPatient();
			case "2" -> inquiryPatient();
			case "3" -> {
				return;
			}
			}
		}
	}
	public void inquiryPatient() {
		System.out.println();
		System.out.println("-".repeat(20) + "[ 조회 메뉴 ]" + "-".repeat(20));
		System.out.println("1.이름으로 조회 | 2.ID로 조회 | 3.환자 진료내역 보기 | 4.환자 메뉴");
		System.out.print("> 메뉴 선택: ");
		String menuNo = scanner.nextLine();
		// System.out.println();
		switch (menuNo) {
		case "1" -> selectPatientName(); // 이름으로 조회
		case "2" -> selectPatientId(); // id로 조회
		case "3" -> selectPatientAppointment(); // 환자 진료내역 보기
		case "4" -> {
			return;
		}
		}
	}

	// 1. 환자 등록
	public void registPatient() {
		Patient patient = new Patient();
		System.out.println("[ 환자 등록 ]");
		System.out.print("- 이름: ");
		patient.setName(scanner.nextLine());
		System.out.print("- 성별 (남/여): ");
		patient.setSex(scanner.nextLine());
		System.out.print("- 전화번호 (010-1234-5678): ");
		patient.setTel(scanner.nextLine());
		System.out.print("- 주소 (OO시 OO구): ");
		patient.setAddress(scanner.nextLine());
		System.out.print("- 생일 (2025/04/30): ");
		patient.setBirth(scanner.nextLine());

		System.out.println("--------------------------------------");
		System.out.print("<확정> 1. Ok | 2. Cancel : ");
		String menuNo = scanner.nextLine();

		if (menuNo.equals("1")) {
			try {
				String sql = "INSERT INTO PATIENT (PATIENT_ID,NAME,SEX,TEL,ADDRESS,BIRTH) VALUES (PATIENT_SEQ.NEXTVAL,?,?,?,?,TO_DATE(?,'yyyy/mm/dd'))";
				PreparedStatement pstmt = conn.prepareStatement(sql);

				pstmt.setString(1, patient.getName());
				pstmt.setString(2, patient.getSex());
				pstmt.setString(3, patient.getTel());
				pstmt.setString(4, patient.getAddress());
				pstmt.setString(5, patient.getBirth());

				int rows = pstmt.executeUpdate();

				if (rows == 1) {
					System.out.println("< 환자 등록이 완료되었습니다 >");
				} else {
					System.out.println("< 환자 등록이 불가합니다 >");
				}
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 2.환자 단건 조회


	// 3-1.환자 이름 조회
	public void selectPatientName() {

		System.out.println();
		System.out.println("[ 환자 정보 조회 (이름) ]");
		System.out.print("- 조회할 환자 이름: ");

		try {
			String sql3 = "SELECT patient_id, name, sex, tel, address, birth FROM PATIENT WHERE name = ? ORDER BY birth"; // prepared
																															// statement
																															// ->
																															// 보안성
																															// 굿
			PreparedStatement pstmt3 = conn.prepareStatement(sql3);
			String name = scanner.nextLine();
			pstmt3.setString(1, name);
			ResultSet rs = pstmt3.executeQuery();

			System.out.printf("%-4s | %-6s | %-4s | %-8s | %-12s | %-10s\n", "환자번호", "환자이름", "성별", "전화번호", "주소",
					"생년월일");
			System.out.println("-".repeat(100));

			boolean result = false; // 결과 유무 확인

			while (rs.next()) {

				result = true;
				Patient patient = new Patient();
				patient.setPatientId(rs.getInt("patient_id"));
				patient.setName(rs.getString("name"));
				patient.setSex(rs.getString("sex"));
				patient.setTel(rs.getString("tel"));
				patient.setAddress(rs.getString("address"));
				patient.setBirth(rs.getDate("birth").toString());

				System.out.printf("%-8d | %-6s | %-4s | %-12s | %-12s | %-15s\n", patient.getPatientId(),
						patient.getName(), patient.getSex(), patient.getTel(), patient.getAddress(),
						patient.getBirth());
			}

			if (!result) {
				System.out.println("<해당 환자가 존재하지 않습니다.>");
			}

			rs.close();
			pstmt3.close();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	// 3-2.환자 ID 상세 조회
	public void selectPatientId() {
		// System.out.println();
		System.out.println("[ 환자 정보 조회 (ID) ]");
		System.out.print("- 조회할 환자 ID: ");

		try {
			String sql = ""
					+ "SELECT pa.patient_id as patient_id, pa.name as pa_name, pa.sex, pa.tel, pa.address, pa.birth, "
					+ "re.reservation_id, re.visit_date, de.name as de_name " + "FROM patient pa "
					+ "LEFT JOIN reservation re ON pa.patient_id = re.patient_id "
					+ "LEFT JOIN department de ON re.department_id = de.department_id " + "WHERE pa.patient_id = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(scanner.nextLine()));
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				Patient patient = new Patient();
				Reservation reservation = new Reservation();
				Department department = new Department();

				patient.setPatientId(rs.getInt("patient_id"));
				patient.setName(rs.getString("pa_name"));
				patient.setSex(rs.getString("sex"));
				patient.setTel(rs.getString("tel"));
				patient.setAddress(rs.getString("address"));
				patient.setBirth(rs.getDate("birth").toString());
				reservation.setReservationId(rs.getInt("reservation_id"));
				Date visitDate = rs.getDate("visit_date");
				if (visitDate != null) {
					reservation.setVisitDate(visitDate.toString());
				} else {
					reservation.setVisitDate("방문기록 없음");
				}
				department.setName(rs.getString("de_name"));

				System.out.printf("%-4s | %-6s | %-4s | %-8s | %-12s | %-10s | %-4s | %-10s | %s\n", "환자번호", "환자이름",
						"성별", "전화번호", "주소", "생년월일", "예약번호", "예정방문일자", "지정 과");
				System.out.println("-".repeat(100));
				System.out.printf("%-8d | %-6s | %-4s | %-12s | %-12s | %-15s | %-8d | %-15s | %s\n",
						patient.getPatientId(), patient.getName(), patient.getSex(), patient.getTel(),
						patient.getAddress(), patient.getBirth(), reservation.getReservationId(),
						reservation.getVisitDate(), department.getName());
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 3-3.환자 진료 내역 조회
	public void selectPatientAppointment() {
		System.out.println();
		System.out.println("-".repeat(20) + "[환자 진료 내역 조회]" + "-".repeat(20));
		System.out.print("- 환자 번호 입력 : ");
		int bno = Integer.parseInt(scanner.nextLine());
		try {
			String sql = "SELECT pa.patient_id as p_id, pa.name as p_name, doc.doctor_id as d_id, doc.name as d_name, ap.appointment_id as app_id, TO_CHAR(ap.appointment_date, 'yyyy-mm-dd') as app_date, ap.description as description "
					+ "FROM patient pa JOIN appointment ap ON ap.patient_id = pa.patient_id "
					+ "    JOIN doctor doc ON ap.doctor_id = doc.doctor_id " + "WHERE pa.patient_id = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bno);
			ResultSet rs = pstmt.executeQuery();
			boolean result = false; // 결과 유무 확인
			System.out.printf("%-4s | %-10s | %-4s | %-10s | %-4s | %-10s | %-20s\n", "환자ID", "환자명", "의사ID", "의사명",
					"예약ID", "예약일", "증상설명");
			// 구분선 출력
			System.out
					.println("--------------------------------------------------------------------------------------");
			while (rs.next()) {
				result = true;
				System.out.printf("%-6d | %-10s | %-6d | %-10s | %-6d | %-15s | %-20s\n", rs.getInt("p_id"),
						rs.getString("p_name"), rs.getInt("d_id"), rs.getString("d_name"), rs.getInt("app_id"),
						rs.getString("app_date"), rs.getString("description"));
			}
			if (!result) {
				System.out.println("<해당 환자가 존재하지 않습니다.>");
			}

			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}