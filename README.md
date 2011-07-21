This is a simple uri parser, we can use it to parse a uri and get the params and other information.

**Only one java file, no any other dependency**

Usage
=====

    String url = "http://user:password@host:80/aaa/bbb;xxx=xxx?eee=111&fff=222&fff=333";

    URLParser parser = new URLParser(url);

    // get basic infomation
    System.out.println(parser.getHost());
    System.out.println(parser.getPort());
    System.out.println(parser.getProtocol());
    System.out.println(parser.getPath());
    System.out.println(parser.getUserInfo());
    System.out.println(parser.getCharset());

    // get paramsa
    System.out.println(parser.getParam("eee"));
    System.out.println(parser.getParam("fff"));
    System.out.println(parser.getParams("fff"));

    // update params
    parser.removeParams("eee");
    parser.addParam("ggg", "444");
    parser.updateParams("fff", "555");

    // create query string
    System.out.println(parser.createQueryString());

    // full url
    System.out.println(parser.toString());