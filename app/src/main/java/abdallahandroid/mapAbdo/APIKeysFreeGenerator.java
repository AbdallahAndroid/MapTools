package abdallahandroid.mapAbdo;

public class APIKeysFreeGenerator {

    private static int lastUsedAPINumber = 0;


    //f حل مشكلة التي مجانا لها فقط مرة واحدة استخدام في اليوم مجانا
    private static String projectMapTools = "AIzaSyA3GA8vGWGQRlYC1bucYdYA9vL6tQxFfpw";
    private static String projectMapLearn = "AIzaSyA-BZYlChlyBmN20t_hwaKLBSSKnLdEYow"; //debyg api فيها كثير من الاستخدامات ولا تقف علي واحدة فقط
    private static String projectAcademyInstructor = "AIzaSyBxNcB-piSV_eeJkvcYiPJNMYqdL25d69A";
    private static String projectAcademyStudent = "AIzaSyAwZ8favQLQFPpEl5GRcr2X2GffN-4Pa-s";
//    private static String project1111 = "";
//    private static String project11111 = "";
//    private static String project121 = "";
//    private static String project1221 = "";
//    private static String project1441 = "";


    public static String getNextFreeeApi(){
        String result = null;
        switch (lastUsedAPINumber){
            case 0: result = projectMapTools; break;
            case 1: result = projectMapLearn; break;
            case 2: result = projectAcademyInstructor; break;
            case 3: result = projectAcademyStudent; break;


            default: result = null; break;
        }
        System.out.println("abdoAminDrawRoutes - APIKeysFreeGenerator() usedAPiNumber: " + lastUsedAPINumber);
        //incrment
        lastUsedAPINumber++;
        //result
        return result;
    }

}
