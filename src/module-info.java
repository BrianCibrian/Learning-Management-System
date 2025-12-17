module FoundationsF25 {
	requires javafx.controls;
	requires javafx.graphics;
	requires java.sql;
	requires javafx.base;
    requires org.junit.jupiter.api;
    
	opens entityClasses to javafx.base;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
	opens guiAdminHome to javafx.graphics;
    opens guiListUsers to javafx.graphics;
    opens database to org.junit.platform.commons;
}