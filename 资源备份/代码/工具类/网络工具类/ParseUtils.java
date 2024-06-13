/**
 * 页面解析工具类
 *
 * @author
 * @from
 */
public class ParseUtils {


    // 通过Jsoup解析Html获取 图片
    public static void main(String[] args) throws IOException {
        int current = 1;
        String searchText = "小黑子";
        String url = "https://cn.bing.com/images/search?q=+"+searchText+"&first=" + current;
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".iuscp.isv");
        // List<Picture> pictures = new ArrayList<>();
        for (Element element : elements) {
            // 取图片地址（murl）
            String m = element.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
//            System.out.println(murl);
            // 取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
//            System.out.println(title);
// 			Picture picture = new Picture();
// 			picture.setTitle(title);
// 			picture.setUrl(murl);
// 			pictures.add(picture);
            System.out.println(murl);
            System.out.println(title);
        }
        // System.out.println(pictures);
    }

}