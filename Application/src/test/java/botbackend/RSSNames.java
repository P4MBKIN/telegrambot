package botbackend;

public enum RSSNames {
    POLITICS, KINO, NAUKA_I_TECHNICA, CORPORATIONS_I_FIRMS,
    MUSICA, PUTESHESTVIJA, SPORT, SHOW_BUSINESS, ZDOROVIE,
    CULTURA;

    public String ID(){
        switch (name()){
            case "POLITICS":
                return "https://news.rambler.ru/rss/politics";
            case "KINO":
                return "https://news.yandex.ru/movies.rss";
            case "NAUKA_I_TECHNICA":
                return "https://news.rambler.ru/rss/tech";
            case "CORPORATIONS_I_FIRMS":
                return "https://news.rambler.ru/rss/business";
            case "MUSICA":
                return "https://news.yandex.ru/music.rss";
            case "PUTESHESTVIJA":
                return "https://news.yandex.ru/travels.rss";
            case "SPORT":
                return "https://news.yandex.ru/sport.rss";
            case "SHOW_BUSINESS":
                return "https://news.rambler.ru/rss/starlife";
            case "ZDOROVIE":
                return "https://news.yandex.ru/health.rss";
            case "CULTURA":
                return "https://news.yandex.ru/culture.rss";
            default:
                return "0";
        }
    }
}
