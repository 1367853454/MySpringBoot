package tk.mybatis;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 自己的Mapper
 * 特别注意，该接口不能被扫描到，否则会出错
 * 不能被spring扫描到，就是不要放在@SpringBootApplication注解的类的路径下
 * @param <T>
 */

public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}