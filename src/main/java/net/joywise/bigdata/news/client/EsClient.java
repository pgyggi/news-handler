package net.joywise.bigdata.news.client;

import java.net.InetAddress;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class EsClient {
	private static TransportClient client;
	public static void main(String[] args) {
		initialEs("es-cluster","192.168.20.252","9300");
//		String json="{\"url\":\"http://report.iresearch.cn/report/201712/3102.shtml\",\"title\":\"2016年洞察趣萌晒图人群，紧跟场景营销报告\",\"profession\":\"新营销\",\"report_time\":\"2017/12/12 16:23:46\",\"crawl_time\":\"2017/12/13 10:23:46\",\"source\":\"艾瑞咨询\"}";
		String json="{\"url\":\"http://d.g.wanfangdata.com.cn/Periodical_21sjjzcl201512006.aspx\",\"title\":\"浅析模板早拆技术在建筑施工中的应用dddd\",\"user_keyword\":\"佳木斯大学\",\"cited\":\"4\",\"author\":\"赵春武,高照峰,贾宇军,张兆国,\",\"collect_info\":\"\",\"paper_date\":\"2017/11/30\",\"crawl_time\":\"2017/12/13 10:23:46\",\"abstract\":\"模板早拆技术是建筑模板工程中经常用到的一项技术,其可以保证模板工程的施工质量与效率,有利于降低建筑工程的施工成本.模板工程在建筑工程中费用比较高的工程,其施工的费用约占建筑工程总费用的1/3左右,在模板工程中,...\",\"keyword\":\"关键词：模板早拆   技术   建筑施工   应用\",\"author_affiliation\":\"黑龙江恒泰建设集团有限公司,黑龙江佳木斯,154005 \"}";
		add("diting","paper",null,json);
	}

	public static void initialEs(String clusterName, String nodeIp, String port) {
		try {
			// 设置集群名称
			Settings settings = Settings.builder().put("cluster.name", clusterName).build();
			// 创建client
			InetSocketTransportAddress address = new InetSocketTransportAddress(InetAddress.getByName(nodeIp),
					Integer.parseInt(port));
			client = TransportClient.builder().settings(settings).build().addTransportAddress(address);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean add(String index, String type, String id, String json) {
		IndexResponse response = null;
		try {
			response = client.prepareIndex(index, type).setId(id).setSource(json).execute().actionGet();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return response.isCreated();
	}
}
