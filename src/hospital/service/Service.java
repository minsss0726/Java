package hospital.service;

import java.sql.Connection;
import java.util.Scanner;

public class Service {
	protected Scanner scanner = null;
	protected Connection conn = null;

	public Service(Scanner scanner, Connection conn) {
		this.scanner = scanner;
		this.conn = conn;
	}
}
