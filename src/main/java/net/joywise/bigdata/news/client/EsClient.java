package net.joywise.bigdata.news.client;

import java.net.InetAddress;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class EsClient {
	private static TransportClient client;

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
