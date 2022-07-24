package webmonitor;

public class WebMonitorMain {

	public static void main(String[] args) {
		VictorFreeMobilePlanMonitor webmon1 = new VictorFreeMobilePlanMonitor();
		webmon1.run();
		
		BosePromoMonitor webmon2 = new BosePromoMonitor();
		webmon2.run();
	}

}
