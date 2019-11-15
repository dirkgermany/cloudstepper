package com.dam.coach.textPreparation;

import java.util.HashMap;
import java.util.Map;

public class Replacements {
	private static Map<String, String> replacementMap = new HashMap<>();
	
	private final static String EDITORIAL_INFO_NEWS = "Der US-amerikanische Präsident Donald Trump und sein chinesischer Amtskollege Xi Jinping sind sich bei der Einigung des Handelskonflikts in den vergangenen Tagen näher gekommen, was zu steigenden Aktienmärkten geführt hat. Tarife und Zölle schmälern die Unternehmensgewinne, weil die Kauflaune der Bevölkerung auf Grund von höheren Preisen sinkt. Höhere Preise = weniger Nachfrage = weniger Umsatz der Unternehmen. Außerdem haben in den letzten Wochen viele Unternehmen eine gute Gewinnentwicklung gezeigt.";
	private final static String EDITORIAL_PORTFOLIO_STATISTICS_0 = "3.000";
	private final static String EDITORIAL_PORTFOLIO_STATISTICS_1 = "60";
	private final static String EDITORIAL_PORTFOLIO_STATISTICS_2 = "USA";
	private final static String EDITORIAL_INFO_INVESTMAL_BASICS_0 = "Das Konzept nennt sich Diversifikation. Du legst nicht nur in einige wenige Aktien an, sondern streust Dein Geld breit in verschiedene Anlageklassen wie Aktien, Anleihen und Gold. Außerdem sind die Anlagen auf über 50 verschiedene Länder verteilt. So machst Du Dein Portfolio nicht so abhängig von einzelnen großen Ereignissen.";
	private final static String EDITORIAL_INFO_BOND = "Eine Anleihe berechtigt - im Gegensatz zu einer Aktie - zum Erhalt eines jährlichen Kupons. Das kannst Du dir als eine Art Verzinsung vorstellen. Im Gegensatz zu einer Aktie bist Du damit aber nicht Teileigentümer des Unternehmens.";
	private final static String EDITORIAL_INFO_INVESTMAL_BASICS_1 = "Unser Ziel ist, dass Dein Geld langfristig produktiv für Dich arbeitet. Dafür legen wir es in Portfolios an.";
	private final static String EDITORIAL_INFO_INVESTMAL_COACH = "Meine Aufgabe als Coach ist, Dir jederzeit mit interessanten Nachrichten zu deinem Portfolio zur Verfügung zu stehen.";
	private final static String EDITORIAL_INFO_INVESTMAL_CREDITCARD_0 = "Deine Bankkarte ermöglicht Dir, jederzeit auf Dein Geld zugreifen zu können. Und das überall auf der Welt. Und das Beste daran? Dein Geld ist gleichzeitig investiert und arbeitet für Dich. Zusätzliche Kosten fallen dafür nicht an.";
	private final static String EDITORIAL_INFO_INVESTMAL_CREDITCARD_1 = "Am sinnvollsten ist es, wenn Du Dein Geld über einen langen Zeitraum im Portfolio anlegst, damit es auch möglichst gut für Dich arbeiten kann. Wenn Du unvorhergesehene Ausgaben hast, kannst Du diese aber beispielsweise mit der Bankkarte zahlen, sodass Du keine Angst haben musst, dass Du nicht unmittelbar auf Dein Geld zugreifen kannst. Die Karte arbeitet für Dich!Als einen guten Aufbewahrungsort für Deine Karte sehen wir einen Tresor oder das Kopfkissen, damit das Geld nicht für kurzfristige Konsumentscheidungen genutzt wird. Natürlich kannst Du die investmal Karte aber auch wie andere Karten in Deinem Portemonnaie aufbewahren. Im Vergleich zu Deinem Geld auf dem Girokonto oder dem Bargeld in deinem Portemonnaie, investierst Du mit uns gleichzeitig Dein Geld, mit dem Ziel, mehr Geld daraus zu machen.";
	private final static String EDITORIAL_INFO_INVESTMAL_GOAL = "Unter der Rubrik Traumziele hast Du die Möglichkeit eigenständige Ziele anzulegen, die Du mit dem investierten Geld erreichen möchtest. Beispielsweise ein Auto, eine Weltreise oder die Rente. Von mir erfährst Du auch, wie lange Du wahrscheinlich noch benötigst, bis Du Dein Ziel erreicht hast.";
	private final static String EDITORIAL_INFO_PORTFOLIO = "Du bist unter anderem mit 1,5% deines Portfolios an Apple beteiligt. Mit seinen Smartphones verdient der Konzern weiterhin viel Geld, was die Aktie von Apple um 7% gesteigert hat. Apple ist vor Microsoft übrigens Dein größtes Einzelinvestment.";

	private final static String PORTFOLIO_PERFORMANCE_FIRST_INVEST_DATE = "3,57";
	private final static String BASICS_CAL_LAST_MONTH = "OKTOBER";
	private final static String BASICS_CAL_GREETING= "Guten Morgen";
	private final static String PORTFOLIO_PERFORMANCE_BASIC_CAL_LAST_MONTH_DATE = "3,07";
	private final static String DEPOT_RETURN = "2.467,54";
	private final static String DEPOT_BOND_INVEST_PERCENTAGE = "15";
	
	private static void initMap () {
		replacementMap.put("editorial.info.news", EDITORIAL_INFO_NEWS);
		replacementMap.put("editorial.info.portfolio.basics", EDITORIAL_INFO_PORTFOLIO);
		replacementMap.put("editorial.info.portfolio.statistics(0)", EDITORIAL_PORTFOLIO_STATISTICS_0);
		replacementMap.put("editorial.info.portfolio.statistics(1)", EDITORIAL_PORTFOLIO_STATISTICS_1);
		replacementMap.put("editorial.info.portfolio.statistics(2)", EDITORIAL_PORTFOLIO_STATISTICS_2);
		replacementMap.put("editorial.info.investmal.basics(0)", EDITORIAL_INFO_INVESTMAL_BASICS_0);
		replacementMap.put("editorial.info.investmal.basics(1)", EDITORIAL_INFO_INVESTMAL_BASICS_1);
		replacementMap.put("editorial.info.investmal.coach", EDITORIAL_INFO_INVESTMAL_COACH);
		replacementMap.put("editorial.info.investmal.creditCard(0)", EDITORIAL_INFO_INVESTMAL_CREDITCARD_0);
		replacementMap.put("editorial.info.investmal.creditCard(1)", EDITORIAL_INFO_INVESTMAL_CREDITCARD_1);
		replacementMap.put("editorial.info.investmal.goal", EDITORIAL_INFO_INVESTMAL_GOAL);
		replacementMap.put("editorial.info.loan.basics", EDITORIAL_INFO_BOND);
		
		replacementMap.put("portfolio.performance.firstInvestDate", PORTFOLIO_PERFORMANCE_FIRST_INVEST_DATE);
		replacementMap.put("basics.cal.lastMonthName", BASICS_CAL_LAST_MONTH);
		replacementMap.put("basics.cal.greeting", BASICS_CAL_GREETING);
		replacementMap.put("portfolio.performance(basic.cal.lastMonthDate)", PORTFOLIO_PERFORMANCE_BASIC_CAL_LAST_MONTH_DATE);
		replacementMap.put("depot_return", DEPOT_RETURN);	
		replacementMap.put("depot.loan.investPercentage", DEPOT_BOND_INVEST_PERCENTAGE);
	}
	
	public static String getReplacement(String replacement) {
		if (replacementMap.isEmpty()) {
			initMap();
		}
		return replacementMap.get(replacement);
	}

}
