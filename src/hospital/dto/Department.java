package hospital.dto;


public class Department {
	private int departmentId;
	private String place;
	private int chiefId;
	private String name;

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public int getChiefId() {
		return chiefId;
	}

	public void setChiefId(int chiefId) {
		this.chiefId = chiefId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}