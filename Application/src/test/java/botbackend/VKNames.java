package botbackend;

public enum VKNames {

    POLITICS, KINO, NAUKA_I_TECHNICA, CORPORATIONS_I_FIRMS,
    MUSICA, PUTESHESTVIJA, SPORT, SHOW_BUSINESS, ZDOROVIE,
    CULTURA;

    public int ID(){
        switch (name()){
            case "POLITICS":
                return 20648295;
            case "KINO":
                return 108468;
            case "NAUKA_I_TECHNICA":
                return 31976785;
            case "CORPORATIONS_I_FIRMS":
                return 34168005;
            case "MUSICA":
                return 34384434;
            case "PUTESHESTVIJA":
                return 24565142;
            case "SPORT":
                return 19342584;
            case "SHOW_BUSINESS":
                return 151660297;
            case "ZDOROVIE":
                return 38094239;
            case "CULTURA":
                return 46598842;
            case "FIVE_UMNUCH_MUSLEY":
                return 24713873;
            case "TEST_GROUP":
                return 160880697;
            case "NOVIY_RAP":
                return 29573241;
            case "FOURCH":
                return 45745333;
            default:
                return 0;
        }
    }
}