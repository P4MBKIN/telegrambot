package botbackend;

public enum VKNames {
    NAUKA_I_TECHNICA, FIVE_UMNUCH_MUSLEY, TEST_GROUP, NOVIY_RAP, FOURCH;

    public int ID(){
        switch (name()){
            case "NAUKA_I_TECHNICA":
                return 31976785;
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
