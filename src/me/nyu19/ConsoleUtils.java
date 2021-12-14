package me.nyu19;

public class ConsoleUtils {
    protected static void sleeper(int ms){
        try{
            Thread.sleep(ms);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    protected static void titlePrinter(){
        System.out.println("      ExtraStorage Data Migrator by nyu19");
        System.out.println("｡☆✼★━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━★✼☆｡");
        sleeper(1000);
        System.out.println("\nWARNING: THIS PROGRAM IS NOT FOOL PROOF, MAKE SURE TO HAVE MULTIPLE COPIES OF RAW FILES USED IN THIS MIGRATION PROCESS!");
        sleeper(3000);
        System.out.println("\nThis program will Migrate playerdata from YAML to SQLite for ExtraStorage plugin."+
                "\nFirst you will be prompted to enter path of the \"userdata\" folder which contains all the <playerUUID>.yml files" +
                "\nAfter that you will be promted to enter path of the \"database.db\" which is created by the plugin."+
                "\nYou will need the database.db which is created by the plugin as it contains some caching textures (tbh idk what that is.) \n");
        System.out.print("Do you want to continue? [Y/N] : ");

    }
    protected static void infoUserdataFolder(){
        System.out.println("Now you will have to locate the \"userdata\" folder and paste the path of it below : ");
    }
    protected static void infoDatabaseFile(){
        System.out.println("Now you will have to provide the path to the \"database.db\" : ");
    }
}
