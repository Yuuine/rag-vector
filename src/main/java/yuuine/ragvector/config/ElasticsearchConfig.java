package yuuine.ragvector.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch 配置类，用于初始化 ElasticsearchClient 客户端。
 */
@Slf4j
@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Value("${elasticsearch.username:}")
    private String username;

    @Value("${elasticsearch.password:}")
    private String password;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // 配置认证凭证（如果用户名和密码为空，则不启用认证）
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (!username.isEmpty() && !password.isEmpty()) {
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }

        // 构建低级 REST 客户端
        RestClient restClient = RestClient.builder(new HttpHost(host, port, "http"))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    if (!username.isEmpty()) {
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                    return httpClientBuilder;
                })
                .build();

        // 创建传输层，使用 Jackson JSON 映射器
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        log.info("ElasticsearchClient initialized with host: {}, port: {}", host, port);

        // 返回高级 Elasticsearch 客户端
        return new ElasticsearchClient(transport);
    }
}