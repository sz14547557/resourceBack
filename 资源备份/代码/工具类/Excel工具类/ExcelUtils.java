package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel 相关工具类
 */
@Slf4j
public class ExcelUtils {

    /**
     * excel 转 csv
     *
     * @param multipartFile
     * @return
     */
    public static String excelToCsv(MultipartFile multipartFile) {
//        File file = null;
//        try {
//            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        // 读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误", e);
        }
        if (CollUtil.isEmpty(list)) {
            return "";
        }
        // 转换为 csv
        StringBuilder stringBuilder = new StringBuilder();
        // 读取表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
        // 读取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        excelToCsv(null);
    }


    /**
     * 读取数据
     */
    public static void main(String[] args) {
        // todo 记得改为自己的测试文件
        // String fileName = "E:\\星球项目\\yupao-backend\\src\\main\\resources\\testExcel.xlsx";
        String fileName = "C:\\Users\\sz\\Downloads\\论文清单.xls";


//         todo sz※ 使用监听器方式读取，在读取时可以对数据进行获取  针对大数据量文件
//        readByListener(fileName);

        // todo sz※  使用同步读，使用EasyExcel框架读取excel 提高读取效率       针对小数据量文件
        synchronousRead(fileName);
    }

    /**
     * todo sz※ 监听器读取   适用于数据量比较大的情况，可以先读取到内存中，分批存入数据库中
     *
     * @param fileName
     */
    public static void readByListener(String fileName) {
        // 在读取时将监听器对应传入进去
        EasyExcel.read(fileName, XingQiuTableUserInfo.class, new TableListener()).sheet().doRead();
    }

    /**
     * todo sz※ 同步读，数据量大时会产生卡顿，有导致oom的风险。但是小数据量时快一些   使用head(数据对应类型实体);方法进行同步读，直接返回全部的数据     也可以配合监听器
     *
     * @param fileName
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish

        // 使用EasyExcel框架的read方法读取excel,指定哪个类去读取，返回结果会被存储在指定类中
        List<XingQiuTableUserInfo> totalDataList =
                // EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
                EasyExcel.read(fileName,new TableListener()).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        for (XingQiuTableUserInfo xingQiuTableUserInfo : totalDataList) {
            System.out.println(xingQiuTableUserInfo);
        }
    }


}
