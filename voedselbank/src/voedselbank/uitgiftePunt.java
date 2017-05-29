package voedselbank;
import java.awt.Dimension;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.*;

public class uitgiftePunt {

    private static Connection connection;
    private static Statement statement;
    private static ResultSet rs;
    
    /**
     * Deze methode zorgt er voor dat op basis van de (meta)data in de
     * meegegeven resultset een object wordt voorbereid die door JTable
     * kan worden gebruikt om de data in een grid te tonen.
     * 
     * @param rs - De resultset waaruit de data wordt uitgelezen.
     * @return - Het object met data en metadata dat door JTable wordt
     *      gebruikt om de data gestructureerd te tonen op het scherm.
     * @throws SQLException - Let op: Hier wordt data gelezen en dat kan fout gaan.
     *      Op de plek waar deze methode wordt gebruikt moet de SQLException
     *      worden afgehandeld.
     */
    public static DefaultTableModel fillTable (ResultSet rs) throws SQLException {
        
        // De metadata wordt uit de resultset opgehaald.
        ResultSetMetaData metaData = rs.getMetaData();

        // De namen van de kolommen worden in een een-dimensionale Vector
        // opgeslagen.
        Vector<String> columnNames = new Vector<String> ();
        columnNames.add("Uitgiftepunt");
        columnNames.add("Maximum pakketten");
        columnNames.add("Capaciteit bezet");

        
        // De records met data worden in een 2-dimensionale Vector (waarbij 
        // het aantal rijen gelijk is aan het aantal opgehaalde records en 
        // het aantal kolommen gelijk is aan het aantal opgehaalde kolommen.
        Vector<Vector<Object>> data = new Vector<Vector<Object>> ();
        
        // De data wordt opgehaald en per record uitgelezen.
        while (rs.next()) {
            
            // Er wordt een rij aangemaakt die - nadat de rij is gevuld met data -
            // wordt toegevoegd aan de 2-dimensionale Vector met data.
            //
            // TO DO: Voeg hier de kolommen toe (met de juiste types die vanuit
            // je eigen SQL-Select worden teruggegeven.
            Vector<Object> vector = new Vector<Object>();
            vector.add(rs.getObject ("locatie"));
            vector.add(rs.getObject ("maxPakketten"));
            vector.add("#");
            data.add (vector);
        }

        // De data en metadata wordt in een object ingepakt die door JTable
        // geïnterpreteerd kan worden. JTable vult de eerste rij met 
        // columnNames en de daarop volgende rijen met de hierboven toegevoegde
        // data.
        return new DefaultTableModel (data, columnNames);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Windows feel
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } 
        catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
           //
        }

        //
        
        JFrame frame = new JFrame();
        frame.setSize(1000, 500);
        frame.setTitle("VOEHAA - Uitgiftepunten");
        JPanel panel = new JPanel();

        try {
            connection = SimpleDataSourceV2.getConnection();
            statement = connection.createStatement();

            // TO DO: Voeg hier je eigen query toe.
            String query = "SELECT locatie, maxPakketten FROM UitgiftePunt;";

            rs = statement.executeQuery (query);
            
            JTable table = new JTable (fillTable (rs));
            JScrollPane scrollPane = new JScrollPane (table);
            scrollPane.setPreferredSize(new Dimension(970, 440));
            
            panel.add(scrollPane);
        }
        // Zowel direct in de try als in de methode fillTable (throws SQLException)
        // kan een SQLException ontstaan die moet worden afgehandeld.
        catch (SQLException ex) {
            System.out.println ("Er is iets mis met de database.");
            // Logger.getLogger(Blok4WC2.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame.add (panel);
        // Het frame mag niet direct worden afgesloten als de gebruiker op het
        // rode kruisje klikt. Voordat het scherm wordt gesloten (en daarmee de
        // applicatie) moeten eerst de SQL-objecten en connectie met de database
        // worden afgesloten en/of worden vrij gegeven.
        frame.addWindowListener (new java.awt.event.WindowAdapter() {
            
            @Override
            public void windowClosing (java.awt.event.WindowEvent windowEvent) {

                SimpleDataSourceV2.closeConnection ();
                System.out.println ("De database is succesvol afgesloten");
                System.exit(0);
            }
        });

        frame.setVisible (true);
    }
}
