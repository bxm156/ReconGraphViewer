/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Nov 8, 2008
 * Time: 10:25:13 PM
 * To change this template use File | Settings | File Templates.
 */

package binevi.View;

import java.sql.*;

public class DBWrapper {
    private static String driver ="com.microsoft.sqlserver.jdbc.SQLServerDriver" ;
    private static String server ="jdbc:sqlserver://localhost:1433;databaseName=Pathcase_SystemBiology;user=sa;password=dblab;";
    private static Connection con=null;
    
    public DBWrapper(){
//        try{
//            Class.forName(driver);
//            con= DriverManager.getConnection(server);
//            Statement instruction = con.createStatement();
//            ResultSet resultat = instruction.executeQuery("SELECT * FROM pathways");
//while(resultat.next()){
//    System.out.println(resultat.getString(1));
//    //System.in.read();
//}
//        } catch(Exception e){
//            System.out.println(""+e);
//            System.exit(-1);
//        }finally{
//        //con.close();
//        }

    }

    public static Connection getPFTDBConnection(){
        try{
            Class.forName(driver);
        }catch(java.lang.ClassNotFoundException e){
            System.err.print("ClassNotFoundException:");
            System.err.println(e.getMessage());
        }
        try{
            con= DriverManager.getConnection(server);
        }catch(SQLException ex){
            System.err.println("SQLException:"+ex.getMessage());
        }
        return con;
    }

//    public static void main(String args[]){
//        DBWrapper testdb=new DBWrapper();
//        System.out.println("resultat.getString(0)");
////        try{
////        System.in.read();
////            }catch(Exception e){
////        System.out.println("ok");}
//    }

}
