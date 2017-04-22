package economySimulation;

public class Capital {

	private double value;

	public Capital(double value) {
		if (value < 0) {
			throw new IllegalArgumentException();
		} else {
			this.value = value;
		}
	}

	public double getValue() {
		return value;
	}

	public void setValue(double newValue) {
		if (newValue < 0) {
			throw new IllegalArgumentException();
		} else {
			value = newValue;
		}
	}
}