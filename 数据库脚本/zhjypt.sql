/*
 Navicat Premium Dump SQL

 Source Server         : sjk
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : zhjypt

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 18/03/2026 16:37:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `adm_id` int NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `adm_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员名称',
  `adm_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员账户',
  `adm_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员密码',
  `adm_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员电话',
  `adm_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员邮箱',
  `adm_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员地址',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `del` int NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`adm_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'gr', '13579', '24680', '13413514672', '2767532724@qq.com', '广州', NULL, 0);
INSERT INTO `admin` VALUES (2, '张三', '31453154', '134535', '1345314534', '5555', '555', NULL, 1);
INSERT INTO `admin` VALUES (3, '李四', 'admin_lisi', '123456aA!', '13900139002', 'lisi@zhjypt.com', '上海市浦东新区张江高科技园区', NULL, 0);
INSERT INTO `admin` VALUES (4, '王五', 'admin_wangwu', '123456aA!', '13700137003', 'wangwu@zhjypt.com', '广东省深圳市南山区科技园', NULL, 0);
INSERT INTO `admin` VALUES (5, '赵六', 'admin_zhaoliu', '123456aA!', '13600136004', 'zhaoliu@zhjypt.com', '浙江省杭州市西湖区文二路', NULL, 0);
INSERT INTO `admin` VALUES (6, '孙七', 'admin_sunqi', '123456aA!', '13500135005', 'sunqi@zhjypt.com', '江苏省南京市鼓楼区中山路', NULL, 0);

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `announcement_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `publisher_id` int NOT NULL COMMENT '发布人ID',
  `publisher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布人姓名',
  `publish_time` datetime NOT NULL COMMENT '发布时间',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用，0禁用，1启用',
  `del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`announcement_id`) USING BTREE,
  INDEX `idx_publisher_id`(`publisher_id` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE,
  INDEX `idx_status_del`(`status` ASC, `del` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (1, '系统维护通知', '系统将于本周六晚上10点进行维护，预计维护时间2小时，期间系统将无法访问，请大家提前做好准备。', 1, '系统管理员', '2025-12-24 16:16:33', 1, 0);
INSERT INTO `announcement` VALUES (2, '新功能上线公告', '我们很高兴地宣布，新的在线考试功能已经上线！现在教师可以创建在线考试，学生可以在线答题。欢迎大家体验新功能。', 1, '系统管理员', '2025-12-24 16:16:33', 1, 0);
INSERT INTO `announcement` VALUES (3, '测试', 'aaa', 1, 'gr', '2025-12-24 16:33:15', 1, 0);

-- ----------------------------
-- Table structure for announcement_read
-- ----------------------------
DROP TABLE IF EXISTS `announcement_read`;
CREATE TABLE `announcement_read`  (
  `read_id` int NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `announcement_id` int NOT NULL COMMENT '公告ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户类型：admin/teacher/student',
  `read_time` datetime NOT NULL COMMENT '阅读时间',
  PRIMARY KEY (`read_id`) USING BTREE,
  UNIQUE INDEX `uk_announcement_user`(`announcement_id` ASC, `user_id` ASC, `user_type` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC, `user_type` ASC) USING BTREE,
  INDEX `idx_announcement_id`(`announcement_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告阅读记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement_read
-- ----------------------------
INSERT INTO `announcement_read` VALUES (1, 3, 1, 'student', '2025-12-24 17:38:07');
INSERT INTO `announcement_read` VALUES (2, 2, 1, 'student', '2025-12-24 17:38:10');
INSERT INTO `announcement_read` VALUES (3, 1, 1, 'student', '2025-12-24 17:51:13');
INSERT INTO `announcement_read` VALUES (4, 3, 1, 'teacher', '2025-12-29 15:50:44');
INSERT INTO `announcement_read` VALUES (5, 2, 1, 'teacher', '2025-12-29 15:50:46');
INSERT INTO `announcement_read` VALUES (6, 1, 1, 'teacher', '2025-12-29 15:50:46');

-- ----------------------------
-- Table structure for chapter_content
-- ----------------------------
DROP TABLE IF EXISTS `chapter_content`;
CREATE TABLE `chapter_content`  (
  `content_id` int NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `chapter_id` int NOT NULL COMMENT '章节ID',
  `content_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容标题',
  `content_type` tinyint(1) NOT NULL COMMENT '内容类型：1视频，2文档，3音频，4图片',
  `content_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容URL',
  `duration` int NULL DEFAULT NULL COMMENT '内容时长（秒）',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `content_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容描述',
  `sort_order` int NULL DEFAULT 1 COMMENT '排序顺序',
  `progress_weight` int NOT NULL DEFAULT 1 COMMENT '进度权重（用于计算课程总进度）',
  `content_status` tinyint(1) NULL DEFAULT 0 COMMENT '内容状态：0草稿，1发布',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`content_id`) USING BTREE,
  INDEX `idx_chapter_id`(`chapter_id` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `fk_content_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `course_chapter` (`chapter_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 338 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '章节内容表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chapter_content
-- ----------------------------
INSERT INTO `chapter_content` VALUES (1, 1, 'Java简介和发展历史', 1, 'https://www.w3schools.com/html/mov_bbb.mp4', 10, 788493, '这是一个测试视频，用于演示视频播放功能', 3, 1, 1, '2025-12-29 15:58:54', '2025-12-29 17:59:23', 0);
INSERT INTO `chapter_content` VALUES (2, 1, '开发环境搭建', 1, 'https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4', 1200, 67108864, '开发环境搭建演示视频', 2, 5, 1, '2025-12-29 15:58:54', '2025-12-29 17:34:06', 1);
INSERT INTO `chapter_content` VALUES (3, 1, '第一个Java程序', 1, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 800, 45088768, '第一个Java程序编写演示', 3, 5, 1, '2025-12-29 15:58:54', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (4, 1, 'Java语法参考文档', 2, 'https://www.runoob.com/java/java-tutorial.html', NULL, 2097152, 'Java基础语法参考文档', 4, 3, 1, '2025-12-29 15:58:54', '2025-12-29 17:34:06', 1);
INSERT INTO `chapter_content` VALUES (5, 2, '类和对象的概念', 1, 'https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4', 1500, 83886080, '面向对象编程概念讲解', 1, 5, 1, '2025-12-29 15:58:54', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (6, 2, '继承和多态', 1, 'https://example.com/videos/java-oop-2.mp4', 1800, 100663296, '深入学习继承和多态机制', 2, 5, 1, '2025-12-29 15:58:54', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (7, 5, 'Python安装指南', 1, 'https://example.com/videos/python-install.mp4', 600, 33554432, 'Windows和Mac系统Python安装', 1, 5, 1, '2025-12-29 15:58:54', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (8, 5, 'Jupyter Notebook使用', 1, 'https://example.com/videos/jupyter-intro.mp4', 900, 50331648, '学习使用Jupyter Notebook进行数据分析', 2, 5, 1, '2025-12-29 15:58:54', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (9, 8, 'HTML文档结构', 1, 'https://example.com/videos/html-structure.mp4', 720, 40265318, '了解HTML文档的基本结构', 1, 5, 1, '2025-12-29 15:58:54', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (10, 8, '常用HTML标签', 1, 'https://example.com/videos/html-tags.mp4', 1080, 60397977, '学习常用的HTML标签使用方法', 2, 5, 1, '2025-12-29 15:58:54', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (11, 8, 'HTML练习题', 2, 'https://www.runoob.com/html/html-tutorial.html', NULL, 1048576, 'HTML基础练习题集', 3, 3, 1, '2025-12-29 15:58:54', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (12, 1, 'Java简介和发展历史', 1, 'https://www.w3schools.com/html/mov_bbb.mp4', 10, 788493, '这是一个测试视频，用于演示视频播放功能', 1, 20, 1, '2025-12-29 16:25:41', '2025-12-29 18:06:11', 0);
INSERT INTO `chapter_content` VALUES (13, 1, '开发环境搭建', 1, 'https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4', 1200, 67108864, '开发环境搭建演示视频', 2, 5, 1, '2025-12-29 16:25:41', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (14, 1, '第一个Java程序', 1, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 800, 45088768, '第一个Java程序编写演示', 3, 5, 1, '2025-12-29 16:25:41', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (15, 1, 'Java语法参考文档', 2, 'https://www.runoob.com/java/java-tutorial.html', NULL, 2097152, 'Java基础语法参考文档', 4, 1, 1, '2025-12-29 16:25:41', '2025-12-29 20:01:41', 0);
INSERT INTO `chapter_content` VALUES (16, 2, '类和对象的概念', 1, 'https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4', 1500, 83886080, '面向对象编程概念讲解', 1, 5, 1, '2025-12-29 16:25:41', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (17, 2, '继承和多态', 1, 'https://vd3.bdstatic.com/mda-mi5h9q2qf2k8s4d8/1080p/cae_h264/1629012345678901234/mda-mi5h9q2qf2k8s4d8.mp4', 1800, 100663296, '深入学习继承和多态机制', 2, 5, 0, '2025-12-29 16:25:41', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (18, 5, 'Python安装指南', 1, 'https://vd2.bdstatic.com/mda-mj3h8k5ndaka5etl/1080p/cae_h264/1630123456789012345/mda-mj3h8k5ndaka5etl.mp4', 600, 33554432, 'Windows和Mac系统Python安装', 1, 5, 1, '2025-12-29 16:25:41', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (19, 5, 'Jupyter Notebook使用', 1, 'https://vd4.bdstatic.com/mda-mk8k9q2qf2k8s4d9/1080p/cae_h264/1631234567890123456/mda-mk8k9q2qf2k8s4d9.mp4', 900, 50331648, '学习使用Jupyter Notebook进行数据分析', 2, 5, 1, '2025-12-29 16:25:41', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (20, 8, 'HTML文档结构', 1, 'https://vd1.bdstatic.com/mda-ml5h9q2qf2k8s4da/1080p/cae_h264/1632345678901234567/mda-ml5h9q2qf2k8s4da.mp4', 720, 40265318, '了解HTML文档的基本结构', 1, 5, 1, '2025-12-29 16:25:41', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (21, 8, '常用HTML标签', 1, 'https://vd3.bdstatic.com/mda-mm3h8k5ndaka5etm/1080p/cae_h264/1633456789012345678/mda-mm3h8k5ndaka5etm.mp4', 1080, 60397977, '学习常用的HTML标签使用方法', 2, 5, 1, '2025-12-29 16:25:41', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (22, 8, 'HTML练习题', 2, 'https://www.runoob.com/html/html-tutorial.html', NULL, 1048576, 'HTML基础练习题集', 3, 3, 1, '2025-12-29 16:25:41', '2025-12-29 17:34:06', 0);
INSERT INTO `chapter_content` VALUES (301, 201, '1.1 函数的概念', 1, 'http://localhost:9999/uploads/videos/aca84244-93c6-481d-b72b-3c424fbaee95.mp4', 7200, 43703215, '介绍函数的定义、定义域、值域等基本概念', 1, 80, 1, '2026-01-07 10:48:38', '2026-01-07 11:13:57', 0);
INSERT INTO `chapter_content` VALUES (302, 201, '函数概念课件', 2, 'https://example.com/docs/function-concept.pdf', NULL, 2097152, '函数概念的详细讲解课件', 2, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (303, 201, '函数图像示例', 4, 'https://img2.baidu.com/it/u=3285284586,2265205043&fm=26&fmt=auto', NULL, 524288, '常见函数的图像展示', 3, 40, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (304, 201, '1.2 函数的性质', 1, 'https://vd2.bdstatic.com/mda-pm8kt2k4n513h8qtz/sc/cae_h264/1701140773371726620/mda-pm8kt2k4n513h8qtz.mp4', 1500, 87654321, '学习函数的单调性、奇偶性、周期性等性质', 4, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (305, 201, '函数性质练习题', 2, 'https://example.com/docs/function-properties-exercises.pdf', NULL, 1572864, '函数性质相关练习题', 5, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (306, 201, '1.3 极限的概念', 1, 'https://vd4.bdstatic.com/mda-pm8ku3k4n513h8qtu/sc/cae_h264/1701140773371726621/mda-pm8ku3k4n513h8qtu.mp4', 2100, 123456789, '介绍数列极限和函数极限的定义', 6, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (307, 201, '极限定义详解', 2, 'https://example.com/docs/limit-definition.pdf', NULL, 3145728, '极限ε-δ定义的详细说明', 7, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (308, 201, '1.4 极限的运算法则', 1, 'https://vd1.bdstatic.com/mda-pm8kv4k4n513h8qtv/sc/cae_h264/1701140773371726622/mda-pm8kv4k4n513h8qtv.mp4', 1800, 98765432, '学习极限的四则运算法则和复合函数极限', 8, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (309, 201, '极限计算方法总结', 2, 'https://example.com/docs/limit-calculation-methods.pdf', NULL, 2621440, '常用极限计算方法汇总', 9, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (310, 202, '2.1 导数的定义', 1, 'https://vd3.bdstatic.com/mda-pm8kw5k4n513h8qtw/sc/cae_h264/1701140773371726623/mda-pm8kw5k4n513h8qtw.mp4', 1650, 96468992, '从几何和物理角度理解导数的定义', 1, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (311, 202, '导数几何意义图解', 4, 'https://img1.baidu.com/it/u=1234567890,0987654321&fm=26&fmt=auto', NULL, 786432, '导数几何意义的图形说明', 2, 40, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (312, 202, '2.2 基本求导法则', 1, 'https://vd2.bdstatic.com/mda-pm8kx6k4n513h8qtx/sc/cae_h264/1701140773371726624/mda-pm8kx6k4n513h8qtx.mp4', 2000, 117440512, '学习基本函数的导数公式和求导法则', 3, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (313, 202, '求导公式表', 2, 'https://example.com/docs/derivative-formulas.pdf', NULL, 1048576, '常用函数导数公式汇总表', 4, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (314, 202, '2.3 复合函数求导（链式法则）', 1, 'https://vd4.bdstatic.com/mda-pm8ky7k4n513h8qty/sc/cae_h264/1701140773371726625/mda-pm8ky7k4n513h8qty.mp4', 1750, 102760448, '掌握复合函数的求导方法', 5, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (315, 202, '链式法则练习', 2, 'https://example.com/docs/chain-rule-exercises.pdf', NULL, 2097152, '复合函数求导练习题', 6, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (316, 202, '2.4 微分的概念与计算', 1, 'https://vd1.bdstatic.com/mda-pm8kz8k4n513h8qtz/sc/cae_h264/1701140773371726626/mda-pm8kz8k4n513h8qtz.mp4', 1400, 82313216, '理解微分的定义和几何意义', 7, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (317, 203, '3.1 罗尔定理与拉格朗日中值定理', 1, 'https://vd3.bdstatic.com/mda-pm8l09k4n513h8qu0/sc/cae_h264/1701140773371726627/mda-pm8l09k4n513h8qu0.mp4', 2200, 129499136, '学习重要的微分中值定理', 1, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (318, 203, '中值定理证明', 2, 'https://example.com/docs/mean-value-theorem-proof.pdf', NULL, 3670016, '微分中值定理的详细证明', 2, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (319, 203, '3.2 洛必达法则', 1, 'https://vd2.bdstatic.com/mda-pm8l1ak4n513h8qu1/sc/cae_h264/1701140773371726628/mda-pm8l1ak4n513h8qu1.mp4', 1900, 111673344, '使用洛必达法则求未定式极限', 3, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (320, 203, '洛必达法则应用', 2, 'https://example.com/docs/lhopital-rule-applications.pdf', NULL, 2621440, '洛必达法则的典型应用', 4, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (321, 203, '3.3 函数的单调性与极值', 1, 'https://vd4.bdstatic.com/mda-pm8l2bk4n513h8qu2/sc/cae_h264/1701140773371726629/mda-pm8l2bk4n513h8qu2.mp4', 2100, 123731968, '利用导数判断函数的单调性和求极值', 5, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (322, 203, '函数图像分析', 4, 'https://img3.baidu.com/it/u=2345678901,1098765432&fm=26&fmt=auto', NULL, 1048576, '函数单调性和极值的图像分析', 6, 40, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (323, 204, '4.1 不定积分的概念与性质', 1, 'https://vd1.bdstatic.com/mda-pm8l3ck4n513h8qu3/sc/cae_h264/1701140773371726630/mda-pm8l3ck4n513h8qu3.mp4', 1800, 105906176, '理解不定积分的定义和基本性质', 1, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (324, 204, '基本积分公式', 2, 'https://example.com/docs/basic-integral-formulas.pdf', NULL, 1572864, '基本函数的积分公式表', 2, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (325, 204, '4.2 第一类换元积分法', 1, 'https://vd3.bdstatic.com/mda-pm8l4dk4n513h8qu4/sc/cae_h264/1701140773371726631/mda-pm8l4dk4n513h8qu4.mp4', 2000, 117964800, '掌握第一类换元积分法（凑微分法）', 3, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (326, 204, '4.3 第二类换元积分法', 1, 'https://vd2.bdstatic.com/mda-pm8l5ek4n513h8qu5/sc/cae_h264/1701140773371726632/mda-pm8l5ek4n513h8qu5.mp4', 1950, 114688000, '学习第二类换元积分法（变量替换）', 4, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (327, 204, '4.4 分部积分法', 1, 'https://vd4.bdstatic.com/mda-pm8l6fk4n513h8qu6/sc/cae_h264/1701140773371726633/mda-pm8l6fk4n513h8qu6.mp4', 1750, 103219200, '掌握分部积分法的应用技巧', 5, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (328, 204, '积分方法总结', 2, 'https://example.com/docs/integration-methods-summary.pdf', NULL, 3145728, '不定积分方法总结与选择策略', 6, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (329, 205, '5.1 定积分的定义', 1, 'https://vd1.bdstatic.com/mda-pm8l7gk4n513h8qu7/sc/cae_h264/1701140773371726634/mda-pm8l7gk4n513h8qu7.mp4', 1600, 94371840, '从几何角度理解定积分的定义', 1, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (330, 205, '定积分几何意义', 4, 'https://img4.baidu.com/it/u=3456789012,2109876543&fm=26&fmt=auto', NULL, 1310720, '定积分的几何意义图解', 2, 40, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (331, 205, '5.2 牛顿-莱布尼茨公式', 1, 'https://vd3.bdstatic.com/mda-pm8l8hk4n513h8qu8/sc/cae_h264/1701140773371726635/mda-pm8l8hk4n513h8qu8.mp4', 1850, 109051904, '学习定积分的基本计算公式', 3, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (332, 205, '定积分计算练习', 2, 'https://example.com/docs/definite-integral-exercises.pdf', NULL, 2621440, '定积分计算练习题集', 4, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (333, 205, '5.3 定积分的性质与计算技巧', 1, 'https://vd2.bdstatic.com/mda-pm8l9ik4n513h8qu9/sc/cae_h264/1701140773371726636/mda-pm8l9ik4n513h8qu9.mp4', 1700, 100270080, '掌握定积分的重要性质和计算方法', 5, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (334, 206, '6.1 定积分在几何中的应用', 1, 'https://vd4.bdstatic.com/mda-pm8lajk4n513h8qua/sc/cae_h264/1701140773371726637/mda-pm8lajk4n513h8qua.mp4', 2000, 118489600, '计算平面图形的面积和旋转体的体积', 1, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (335, 206, '面积计算示例', 2, 'https://example.com/docs/area-calculation-examples.pdf', NULL, 2097152, '平面图形面积计算实例', 2, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (336, 206, '6.2 定积分在物理中的应用', 1, 'https://vd1.bdstatic.com/mda-pm8lbkk4n513h8qub/sc/cae_h264/1701140773371726638/mda-pm8lbkk4n513h8qub.mp4', 1900, 112197632, '学习定积分在物理问题中的应用', 3, 80, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `chapter_content` VALUES (337, 206, '物理应用综合', 2, 'https://example.com/docs/physics-applications.pdf', NULL, 3670016, '定积分物理应用综合练习', 4, 60, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);

-- ----------------------------
-- Table structure for consultation
-- ----------------------------
DROP TABLE IF EXISTS `consultation`;
CREATE TABLE `consultation`  (
  `consultation_id` int NOT NULL AUTO_INCREMENT COMMENT '咨询ID',
  `student_id` int NOT NULL COMMENT '学生ID',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `teacher_id` int NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师姓名',
  `course_id` int NULL DEFAULT NULL COMMENT '课程ID',
  `course_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程标题',
  `question_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题标题',
  `question_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题详细描述',
  `question_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '问题图片URL',
  `reply_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '教师回复内容',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0待回复，1已回复，2已关闭',
  `priority` tinyint NOT NULL DEFAULT 0 COMMENT '优先级：0普通，1紧急',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '学生是否已读回复：0未读，1已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`consultation_id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '在线咨询表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of consultation
-- ----------------------------
INSERT INTO `consultation` VALUES (1, 1, '张三', 1, '张三', 1, 'Java基础入门', 'Java中的多态如何理解？', '老师您好，我在学习Java面向对象编程时，对多态的概念不太理解，能否举例说明一下？', NULL, '多态是指同一个方法调用，由于对象不同可能会有不同的行为。例如：父类引用指向子类对象时，调用重写的方法会执行子类的实现。这是运行时多态的体现。', '2026-01-12 10:30:00', 1, 0, 1, '2026-01-12 09:00:00', '2026-01-12 10:30:00', 0);
INSERT INTO `consultation` VALUES (2, 1, '张三', 1, '张三', 1, 'Java基础入门', '如何理解Java中的接口？', '接口和抽象类有什么区别？什么时候应该使用接口？', NULL, NULL, NULL, 2, 1, 0, '2026-01-12 11:00:00', '2026-01-25 21:28:01', 1);

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `course_id` int NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程标题',
  `course_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '课程描述',
  `course_cover` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程封面图片URL',
  `category_id` int NOT NULL COMMENT '课程分类ID',
  `teacher_id` int NOT NULL COMMENT '授课教师ID',
  `course_duration` int NULL DEFAULT 0 COMMENT '课程时长（分钟）',
  `student_count` int NULL DEFAULT 0 COMMENT '学生数量',
  `course_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '课程状态：0草稿，1发布，2下架',
  `difficulty_level` tinyint(1) NULL DEFAULT 1 COMMENT '难度等级：1初级，2中级，3高级',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`course_id`) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status_del`(`course_status` ASC, `del` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`teach_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 124 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, 'Java基础入门', 'Java编程语言基础教程，适合零基础学员', 'https://bkimg.cdn.bcebos.com/pic/09fa513d269759ee3d6da59a98a254166d224f4a7112', 1, 9, 0, 1, 1, 1, '2025-12-27 16:25:01', '2025-12-29 15:00:21', '2025-12-27 16:31:25', 0);
INSERT INTO `course` VALUES (2, 'Python数据分析', 'Python在数据分析领域的应用', 'https://bkimg.cdn.bcebos.com/pic/faedab64034f78f0f736651f6a611d55b319eac4b685', 2, 7, 0, 0, 1, 2, '2025-12-27 16:25:01', '2026-01-07 10:49:38', NULL, 0);
INSERT INTO `course` VALUES (3, 'UI设计实战', '用户界面设计理论与实践', 'https://t15.baidu.com/it/u=155438546,3053931516&fm=224&app=112&f=JPEG?w=369&h=500', 3, 12, 0, 0, 1, 2, '2025-12-27 16:25:01', '2026-01-07 10:49:48', NULL, 0);
INSERT INTO `course` VALUES (4, '市场营销策略', '现代市场营销理论与案例分析', 'https://q2.itc.cn/images01/20250111/3d004312777a47aeb924b39c60296f63.jpeg', 6, 8, 0, 0, 1, 1, '2025-12-27 16:25:01', NULL, NULL, 0);
INSERT INTO `course` VALUES (5, '英语口语提升', '日常英语口语交流技巧', 'https://yiyaqimeng.com/wp-content/uploads/2023/04/sbs0.jpg', 7, 13, 0, 0, 1, 1, '2025-12-27 16:25:01', '2026-01-07 10:49:52', NULL, 0);
INSERT INTO `course` VALUES (100, '高等数学（上册）', '高等数学是理工科学生的重要基础课程，本课程涵盖函数与极限、导数与微分、积分学等核心内容。通过系统学习，帮助学生建立扎实的数学基础，培养逻辑思维和解决问题的能力。', 'https://img14.360buyimg.com/pop/jfs/t1/129079/23/14520/67927/5f80f027Ed01825fa/e96b61f4e502fa05.jpg', 8, 1, 7200, 0, 1, 2, '2026-01-07 10:48:38', '2026-02-28 21:03:55', NULL, 0);
INSERT INTO `course` VALUES (101, 'Java进阶实战', '在掌握基础语法的前提下，深入学习集合、并发、IO 与项目实战。', 'https://t14.baidu.com/it/u=4241228626,9754933&fm=224&app=112&f=JPEG?w=500&h=500', 1, 12, 480, 0, 1, 2, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (102, 'Python编程基础', '面向零基础的 Python 入门课程，涵盖语法、函数、文件与简单爬虫。', 'https://bkimg.cdn.bcebos.com/pic/78310a55b319ebc4a31399738c26cffc1f1716f4', 1, 6, 360, 0, 1, 1, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (103, '数据结构与算法（Java版）', '常见数据结构与算法题型讲解，提升编程思维与面试能力。', 'https://img14.360buyimg.com/pop/jfs/t1/185329/7/21375/109994/612858b4E6ebfd392/f10a32244ffcdf31.jpg', 1, 6, 600, 0, 1, 3, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (104, '数据分析入门（Pandas）', '使用 Pandas 进行数据清洗、统计分析与可视化实践。', 'https://gips3.baidu.com/it/u=803108731,2653708258&fm=3074&app=3074&f=JPEG', 2, 7, 420, 0, 1, 2, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (105, '机器学习基础', '从零开始理解监督学习与无监督学习的核心算法和应用场景。', 'https://t14.baidu.com/it/u=518727099,2737399356&fm=224&app=112&f=JPEG?w=500&h=500', 2, 4, 540, 0, 1, 3, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (106, 'HTML/CSS 前端页面基础', '掌握常用 HTML 标签与 CSS 布局，能独立完成静态页面。', 'https://bkimg.cdn.bcebos.com/pic/8694a4c27d1ed21b0ef41c381e27cac451da80cb919f', 3, 9, 300, 0, 1, 1, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (107, 'Vue3 前端项目实战', '基于 Vue3 和 Element Plus 完成一个完整的前端管理系统。', 'https://t15.baidu.com/it/u=318674760,4033463990&fm=224&app=112&f=JPEG?w=500&h=500', 3, 4, 480, 0, 1, 2, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (108, 'UI 设计基础', '从零开始学习界面设计原则、配色与常见组件设计。', 'https://bkimg.cdn.bcebos.com/pic/0dd7912397dda144ad34101fb8e5c7a20cf431ad2846', 4, 11, 360, 0, 1, 1, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (109, '产品需求分析与原型设计', '掌握需求分析方法，并使用原型工具完成产品原型。', 'https://bkimg.cdn.bcebos.com/pic/78310a55b319ebc4b7450247206ed8fc1e178b82b89f', 4, 10, 420, 0, 1, 2, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (110, 'Excel 数据处理与统计', '涵盖常用函数、数据透视表与基础数据分析。', 'https://bkimg.cdn.bcebos.com/pic/03087bf40ad162d9f2d335cdbb8cbeec8a136227679c', 5, 11, 300, 0, 1, 1, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (111, 'PPT 演示与汇报设计', '从结构、排版到动画，教你做出专业的演示文稿。', 'https://img1.baidu.com/it/u=351652126,2092084404&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', 5, 3, 240, 0, 1, 1, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (112, '市场营销基础', '理解市场分析、4P 理论与常见营销案例。', 'https://img14.360buyimg.com/pop/jfs/t18628/324/2347919152/134159/920a8824/5af2aaa8Nd7927ef4.jpg', 6, 10, 360, 0, 1, 1, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (113, '项目管理实务', '结合实际案例讲解项目计划、进度控制与风险管理。', 'https://img14.360buyimg.com/pop/jfs/t1/228319/14/14924/319225/661007c5F6ba31426/3e60a57cc4693a02.jpg', 6, 5, 420, 0, 1, 2, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (114, '大学英语听说', '围绕校园与日常生活场景的英语听说训练。', 'https://fltrp-gy.oss-cn-shanghai.aliyuncs.com/gykejianupload/20230331/files/4.jpg', 7, 1, 360, 0, 1, 1, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (115, '英语写作与表达', '提升英语书面表达能力，涵盖作文结构与常用表达。', 'https://bkimg.cdn.bcebos.com/pic/8cb1cb1349540923dd541c68ac14c609b3de9c82e3db', 7, 10, 360, 0, 1, 2, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (116, '高等数学（下册）', '延续上册内容，讲解积分学、级数与微分方程基础。', 'https://img14.360buyimg.com/pop/jfs/t1/376345/25/21854/97677/6952ad1dFca807f6f/520cd986330ada41.jpg', 8, 3, 720, 0, 1, 2, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (117, '线性代数基础', '向量、矩阵、线性方程组与特征值的系统讲解。', 'https://bkimg.cdn.bcebos.com/pic/d833c895d143ad4bd113c5949f5b4dafa40f4bfba814', 8, 5, 540, 0, 1, 2, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (118, '职业规划与简历写作', '帮助学生做好职业定位，并写出有竞争力的简历。', 'https://bkimg.cdn.bcebos.com/pic/bd315c6034a85edf8db18e3676051e23dd54564edfdf', 9, 11, 180, 0, 1, 1, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (119, '求职面试技巧', '常见面试题型解析与实战模拟，提升求职成功率。', 'https://img14.360buyimg.com/n1/s720x720_jfs/t1/168098/29/15972/104605/60667a5aEee5968bd/e23ac5a697e0232b.jpg', 9, 11, 180, 0, 1, 1, '2026-03-02 16:43:08', '2026-03-02 16:43:08', '2026-03-02 16:43:08', 0);
INSERT INTO `course` VALUES (120, '现代汉语基础', '系统学习现代汉语的语音、词汇与语法知识，提高语言运用能力。', 'https://t13.baidu.com/it/u=3589470993,3682083581&fm=224&app=112&f=PNG?w=500&h=500', 10, 4, 360, 0, 1, 1, '2026-03-02 16:44:55', '2026-03-02 16:44:55', '2026-03-02 16:44:55', 0);
INSERT INTO `course` VALUES (121, '古诗文赏析', '精读经典古诗词与文言文篇章，理解其思想内涵与艺术特色。', 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.alicdn.com%2Fimgextra%2Fi3%2F2707201449%2FTB221HBXU.iyKJjy1XbXXc4cXXa_%21%212707201449.jpg&refer=http%3A%2F%2Fimg.alicdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1775035092&t=9f18a41701d55c892680920263f379cd', 10, 12, 420, 0, 1, 2, '2026-03-02 16:44:55', '2026-03-02 16:44:55', '2026-03-02 16:44:55', 0);
INSERT INTO `course` VALUES (122, '写作与表达', '围绕记叙文、议论文与说明文，训练汉语书面表达与结构组织能力。', 'https://bkimg.cdn.bcebos.com/pic/3bf33a87e950352ac65c756bb216ecf2b21193131d00', 10, 2, 360, 0, 1, 2, '2026-03-02 16:44:55', '2026-03-02 16:44:55', '2026-03-02 16:44:55', 0);
INSERT INTO `course` VALUES (123, '阅读理解与答题技巧', '针对考试中的阅读理解题型，总结常见设问方式与答题策略。', 'https://img14.360buyimg.com/pop/jfs/t1/135539/9/23717/70970/621309c5E069225f7/7161b8b00d26d8e7.jpg', 10, 6, 240, 0, 1, 1, '2026-03-02 16:44:55', '2026-03-02 16:44:55', '2026-03-02 16:44:55', 0);

-- ----------------------------
-- Table structure for course_category
-- ----------------------------
DROP TABLE IF EXISTS `course_category`;
CREATE TABLE `course_category`  (
  `category_id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `category_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态，0禁用，1启用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `idx_status_del`(`status` ASC, `del` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_category
-- ----------------------------
INSERT INTO `course_category` VALUES (1, '编程开发', '编程语言与软件开发相关课程', 1, 1, '2026-03-02 16:37:17', 0);
INSERT INTO `course_category` VALUES (2, '数据科学与AI', '数据分析、机器学习、人工智能相关课程', 2, 1, '2026-03-02 16:37:17', 0);
INSERT INTO `course_category` VALUES (3, 'Web与前端技术', 'Web 开发、前端框架与网站开发相关课程', 3, 1, '2026-03-02 16:37:17', 0);
INSERT INTO `course_category` VALUES (4, '设计与产品', 'UI/UX 设计、产品设计与原型设计相关课程', 4, 1, '2026-03-02 16:37:17', 0);
INSERT INTO `course_category` VALUES (5, '办公与效率', 'Office 办公软件与效率工具相关课程', 5, 1, '2026-03-02 16:37:17', 0);
INSERT INTO `course_category` VALUES (6, '商业与管理', '管理学、市场营销、运营与项目管理相关课程', 6, 1, '2026-03-02 16:37:17', 0);
INSERT INTO `course_category` VALUES (7, '语言学习', '英语等外语学习与语言能力提升课程', 7, 1, '2026-03-02 16:37:17', 0);
INSERT INTO `course_category` VALUES (8, '数学与基础学科', '高等数学、统计学等基础学科学习课程', 8, 1, '2026-03-02 16:37:17', 0);
INSERT INTO `course_category` VALUES (9, '职业发展与通识', '职业规划、求职技巧与通识素养相关课程', 9, 1, '2026-03-02 16:37:17', 0);

-- ----------------------------
-- Table structure for course_category_backup
-- ----------------------------
DROP TABLE IF EXISTS `course_category_backup`;
CREATE TABLE `course_category_backup`  (
  `category_id` int NOT NULL DEFAULT 0 COMMENT '分类ID',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `category_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态，0禁用，1启用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_category_backup
-- ----------------------------
INSERT INTO `course_category_backup` VALUES (1, '编程开发', '编程语言和开发技术相关课程', 1, 1, '2025-12-27 16:25:01', 0);
INSERT INTO `course_category_backup` VALUES (2, '数据科学', '数据分析、机器学习等课程', 2, 1, '2025-12-27 16:25:01', 0);
INSERT INTO `course_category_backup` VALUES (3, '设计创意', '平面设计、UI/UX设计等课程', 3, 1, '2025-12-27 16:25:01', 0);
INSERT INTO `course_category_backup` VALUES (4, '商业管理', '管理学、市场营销等课程', 4, 1, '2025-12-27 16:25:01', 0);
INSERT INTO `course_category_backup` VALUES (5, '语言学习', '外语学习相关课程', 5, 1, '2025-12-27 16:25:01', 0);
INSERT INTO `course_category_backup` VALUES (6, '编程开发', '编程语言和开发技术相关课程', 1, 1, '2025-12-29 15:58:54', 0);
INSERT INTO `course_category_backup` VALUES (7, '设计创意', '平面设计、UI/UX设计等创意类课程', 2, 1, '2025-12-29 15:58:54', 0);
INSERT INTO `course_category_backup` VALUES (8, '数据科学', '数据分析、机器学习、人工智能相关课程', 3, 1, '2025-12-29 15:58:54', 0);
INSERT INTO `course_category_backup` VALUES (9, '商业管理', '企业管理、市场营销、项目管理等商业课程', 4, 1, '2025-12-29 15:58:54', 0);
INSERT INTO `course_category_backup` VALUES (10, '语言学习', '外语学习、语言技能提升课程', 5, 1, '2025-12-29 15:58:54', 0);
INSERT INTO `course_category_backup` VALUES (11, '编程开发', '编程语言和开发技术相关课程', 1, 1, '2025-12-29 16:25:41', 0);
INSERT INTO `course_category_backup` VALUES (12, '设计创意', '平面设计、UI/UX设计等创意类课程', 2, 1, '2025-12-29 16:25:41', 0);
INSERT INTO `course_category_backup` VALUES (13, '数据科学', '数据分析、机器学习、人工智能相关课程', 3, 1, '2025-12-29 16:25:41', 0);
INSERT INTO `course_category_backup` VALUES (14, '商业管理', '企业管理、市场营销、项目管理等商业课程', 4, 1, '2025-12-29 16:25:41', 0);
INSERT INTO `course_category_backup` VALUES (15, '语言学习', '外语学习、语言技能提升课程', 5, 1, '2025-12-29 16:25:41', 0);

-- ----------------------------
-- Table structure for course_chapter
-- ----------------------------
DROP TABLE IF EXISTS `course_chapter`;
CREATE TABLE `course_chapter`  (
  `chapter_id` int NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `chapter_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '章节标题',
  `chapter_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '章节描述',
  `sort_order` int NULL DEFAULT 1 COMMENT '章节排序',
  `chapter_status` tinyint(1) NULL DEFAULT 0 COMMENT '章节状态：0草稿，1发布',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`chapter_id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `fk_chapter_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 207 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程章节表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_chapter
-- ----------------------------
INSERT INTO `course_chapter` VALUES (1, 1, 'Java基础语法', '学习Java的基本语法，包括变量、数据类型、运算符等', 1, 1, '2025-12-29 15:58:54', '2025-12-29 15:58:54', 0);
INSERT INTO `course_chapter` VALUES (2, 1, '面向对象编程', '深入理解Java的面向对象特性，类、对象、继承、多态等··', 2, 1, '2025-12-29 15:58:54', '2025-12-29 16:10:41', 0);
INSERT INTO `course_chapter` VALUES (3, 1, '异常处理与IO', '学习Java异常处理机制和文件IO操作', 3, 1, '2025-12-29 15:58:54', '2025-12-29 15:58:54', 0);
INSERT INTO `course_chapter` VALUES (4, 1, '集合框架', '掌握Java集合框架的使用，List、Set、Map等', 4, 0, '2025-12-29 15:58:54', '2025-12-29 16:14:05', 1);
INSERT INTO `course_chapter` VALUES (5, 2, 'Python环境搭建', '安装Python环境，配置开发工具', 1, 1, '2025-12-29 15:58:54', '2025-12-29 15:58:54', 0);
INSERT INTO `course_chapter` VALUES (6, 2, 'Pandas数据处理', '使用Pandas进行数据清洗和处理', 2, 1, '2025-12-29 15:58:54', '2025-12-29 15:58:54', 0);
INSERT INTO `course_chapter` VALUES (7, 2, '数据可视化', '使用matplotlib和seaborn进行数据可视化', 3, 0, '2025-12-29 15:58:54', '2025-12-29 15:58:54', 0);
INSERT INTO `course_chapter` VALUES (8, 3, 'HTML基础', '学习HTML标签和文档结构', 1, 1, '2025-12-29 15:58:54', '2025-12-29 15:58:54', 0);
INSERT INTO `course_chapter` VALUES (9, 3, 'CSS样式设计', '掌握CSS样式和布局技术', 2, 1, '2025-12-29 15:58:54', '2025-12-29 15:58:54', 0);
INSERT INTO `course_chapter` VALUES (10, 3, 'JavaScript编程', '学习JavaScript基础语法和DOM操作', 3, 0, '2025-12-29 15:58:54', '2025-12-29 15:58:54', 0);
INSERT INTO `course_chapter` VALUES (11, 1, 'Java基础语法', '学习Java的基本语法，包括变量、数据类型、运算符等', 1, 1, '2025-12-29 16:25:41', '2025-12-29 16:25:41', 0);
INSERT INTO `course_chapter` VALUES (12, 1, '面向对象编程', '深入理解Java的面向对象特性，类、对象、继承、多态等', 2, 1, '2025-12-29 16:25:41', '2025-12-29 16:25:41', 0);
INSERT INTO `course_chapter` VALUES (13, 1, '异常处理与IO', '学习Java异常处理机制和文件IO操作', 3, 1, '2025-12-29 16:25:41', '2025-12-29 16:25:41', 0);
INSERT INTO `course_chapter` VALUES (14, 1, '集合框架', '掌握Java集合框架的使用，List、Set、Map等', 4, 0, '2025-12-29 16:25:41', '2025-12-29 16:25:41', 0);
INSERT INTO `course_chapter` VALUES (15, 2, 'Python环境搭建', '安装Python环境，配置开发工具', 1, 1, '2025-12-29 16:25:41', '2025-12-29 16:25:41', 0);
INSERT INTO `course_chapter` VALUES (16, 2, 'Pandas数据处理', '使用Pandas进行数据清洗和处理', 2, 1, '2025-12-29 16:25:41', '2025-12-29 16:25:41', 0);
INSERT INTO `course_chapter` VALUES (17, 2, '数据可视化', '使用matplotlib和seaborn进行数据可视化', 3, 0, '2025-12-29 16:25:41', '2025-12-29 16:25:41', 0);
INSERT INTO `course_chapter` VALUES (18, 3, 'HTML基础', '学习HTML标签和文档结构', 1, 1, '2025-12-29 16:25:41', '2025-12-29 16:25:41', 0);
INSERT INTO `course_chapter` VALUES (19, 3, 'CSS样式设计', '掌握CSS样式和布局技术', 2, 1, '2025-12-29 16:25:41', '2025-12-29 16:25:41', 0);
INSERT INTO `course_chapter` VALUES (20, 3, 'JavaScript编程', '学习JavaScript基础语法和DOM操作', 3, 0, '2025-12-29 16:25:41', '2025-12-29 16:25:41', 0);
INSERT INTO `course_chapter` VALUES (201, 100, '第一章 函数与极限', '介绍函数的基本概念、性质以及极限理论，为后续学习奠定基础', 1, 1, '2026-01-07 10:48:38', '2026-01-07 11:09:27', 0);
INSERT INTO `course_chapter` VALUES (202, 100, '第二章 导数与微分', '学习导数的定义、计算方法和几何意义，掌握微分的概念和应用', 2, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `course_chapter` VALUES (203, 100, '第三章 微分中值定理与导数的应用', '深入理解微分中值定理，学习导数在函数性态分析中的应用', 3, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `course_chapter` VALUES (204, 100, '第四章 不定积分', '学习不定积分的概念、性质和基本积分方法', 4, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `course_chapter` VALUES (205, 100, '第五章 定积分', '掌握定积分的定义、性质、计算方法和几何应用', 5, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `course_chapter` VALUES (206, 100, '第六章 定积分的应用', '学习定积分在几何、物理等领域的实际应用', 6, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);

-- ----------------------------
-- Table structure for course_comment
-- ----------------------------
DROP TABLE IF EXISTS `course_comment`;
CREATE TABLE `course_comment`  (
  `comment_id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `parent_id` int NOT NULL DEFAULT 0 COMMENT '父评论ID，0表示顶级评论',
  `reply_to_id` int NOT NULL DEFAULT 0 COMMENT '被回复的评论ID，0表示不是回复',
  `user_id` int NOT NULL COMMENT '评论用户ID',
  `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户类型：student学生，teacher教师',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户姓名',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `reply_to_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '被回复用户的姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `del` int NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  `is_read` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否已读，0未读，1已读',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC, `user_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_comment
-- ----------------------------
INSERT INTO `course_comment` VALUES (1, 1, 0, 0, 1, 'student', '张三', '999', NULL, '2026-01-12 14:44:18', 0, 1);
INSERT INTO `course_comment` VALUES (2, 1, 1, 1, 1, 'student', '张三', '999', '张三', '2026-01-12 14:44:23', 0, 1);
INSERT INTO `course_comment` VALUES (3, 1, 1, 2, 21, 'student', '000', '888', '张三', '2026-01-12 15:32:19', 0, 1);
INSERT INTO `course_comment` VALUES (4, 1, 1, 2, 21, 'student', '000', '86986', '张三', '2026-01-12 15:32:59', 0, 1);
INSERT INTO `course_comment` VALUES (5, 1, 1, 2, 21, 'student', '000', '689', '张三', '2026-01-12 15:38:37', 0, 1);
INSERT INTO `course_comment` VALUES (6, 1, 1, 2, 21, 'student', '000', '69', '张三', '2026-01-12 15:38:59', 0, 1);

-- ----------------------------
-- Table structure for course_exam
-- ----------------------------
DROP TABLE IF EXISTS `course_exam`;
CREATE TABLE `course_exam`  (
  `course_exam_id` int NOT NULL AUTO_INCREMENT COMMENT '课程习题试卷ID',
  `del` int NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`course_exam_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_exam
-- ----------------------------

-- ----------------------------
-- Table structure for education_suggestion
-- ----------------------------
DROP TABLE IF EXISTS `education_suggestion`;
CREATE TABLE `education_suggestion`  (
  `suggestion_id` int NOT NULL AUTO_INCREMENT COMMENT '建议ID',
  `teacher_id` int NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师姓名',
  `student_id` int NOT NULL COMMENT '学生ID',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `course_id` int NULL DEFAULT NULL COMMENT '课程ID',
  `course_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程标题',
  `suggestion_type` tinyint NOT NULL DEFAULT 1 COMMENT '建议类型：1学习方法，2课程推荐，3学习计划，4其他',
  `suggestion_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '建议标题',
  `suggestion_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '建议内容',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读：0未读，1已读',
  `is_favorite` tinyint NOT NULL DEFAULT 0 COMMENT '是否收藏：0未收藏，1已收藏',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `del` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`suggestion_id`) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教育建议表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of education_suggestion
-- ----------------------------
INSERT INTO `education_suggestion` VALUES (1, 1, '张三', 1, '张三', 1, 'Java基础入门', 1, '关于Java学习的建议', '根据你最近的学习情况，建议你多做一些编程练习，加强对面向对象概念的理解。可以尝试自己设计一些小项目来巩固知识。', 1, 0, '2026-01-12 09:00:00', '2026-01-12 09:30:00', 0);
INSERT INTO `education_suggestion` VALUES (2, 1, '张三', 1, '张三', 100, '高等数学（上册）', 2, '推荐学习资源', '建议你学习完Java基础后，可以继续学习数据结构与算法，这对编程能力的提升很有帮助。', 1, 0, '2026-01-12 10:00:00', '2026-01-25 17:22:58', 0);

-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `exam_id` int NOT NULL AUTO_INCREMENT COMMENT '考试ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `exam_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试标题',
  `exam_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '考试描述',
  `exam_type` tinyint NOT NULL DEFAULT 1 COMMENT '考试类型：1练习，2考试',
  `total_score` int NOT NULL DEFAULT 100 COMMENT '总分',
  `pass_score` int NOT NULL DEFAULT 60 COMMENT '及格分数',
  `time_limit` int NULL DEFAULT NULL COMMENT '考试时长（分钟），NULL表示不限时',
  `attempt_limit` int NULL DEFAULT NULL COMMENT '允许考试次数，NULL表示不限次数',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间，NULL表示随时可考',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间，NULL表示无截止时间',
  `show_result` tinyint NOT NULL DEFAULT 1 COMMENT '是否显示结果：0否，1是',
  `show_answer` tinyint NOT NULL DEFAULT 1 COMMENT '是否显示答案：0否，1是',
  `random_order` tinyint NOT NULL DEFAULT 0 COMMENT '是否随机题目顺序：0否，1是',
  `exam_status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0草稿，1发布',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`exam_id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_exam_status`(`exam_status` ASC) USING BTREE,
  CONSTRAINT `fk_exam_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 205 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam
-- ----------------------------
INSERT INTO `exam` VALUES (1, 1, 'Java基础知识测试', '测试Java基础语法掌握情况', 1, 100, 60, 30, NULL, NULL, NULL, 1, 1, 0, 1, '2026-01-05 21:14:33', '2026-01-05 21:14:33', 0);
INSERT INTO `exam` VALUES (2, 1, 'Java期中考试', 'Java课程期中考试，涵盖前半学期内容', 2, 100, 70, 60, NULL, NULL, NULL, 1, 0, 0, 1, '2026-01-05 21:14:33', '2026-01-05 21:14:33', 0);
INSERT INTO `exam` VALUES (3, 2, 'Python数据分析练习', 'Python数据分析基础练习题', 1, 50, 30, NULL, NULL, NULL, NULL, 1, 1, 0, 1, '2026-01-05 21:14:33', '2026-01-05 21:14:33', 0);
INSERT INTO `exam` VALUES (10, 1, 'Java综合测试（完整版）', '包含所有题型的Java综合测试，涵盖单选、多选、判断、填空、简答题', 2, 100, 60, 90, NULL, NULL, NULL, 1, 1, 0, 1, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `exam` VALUES (11, 1, 'Java基础练习（客观题）', '包含单选、多选、判断题的Java基础练习', 1, 50, 30, 30, NULL, NULL, NULL, 1, 1, 0, 1, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `exam` VALUES (200, 100, '高等数学期中考试', '涵盖函数与极限、导数与微分等前三章内容的综合测试', 2, 100, 60, 120, NULL, NULL, NULL, 1, 1, 0, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `exam` VALUES (201, 100, '高等数学期末考试', '高等数学上册全部内容的综合考试', 2, 120, 72, 150, NULL, NULL, NULL, 1, 1, 0, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `exam` VALUES (202, 100, '函数与极限练习', '第一章函数与极限的基础练习题', 1, 50, 30, 45, NULL, NULL, NULL, 1, 1, 0, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `exam` VALUES (203, 100, '导数与微分练习', '第二章导数与微分的基础练习题', 1, 50, 30, 45, NULL, NULL, NULL, 1, 1, 0, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `exam` VALUES (204, 100, '积分学练习', '不定积分和定积分的综合练习', 1, 60, 36, 60, NULL, NULL, NULL, 1, 1, 0, 1, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);

-- ----------------------------
-- Table structure for exam_question
-- ----------------------------
DROP TABLE IF EXISTS `exam_question`;
CREATE TABLE `exam_question`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` int NOT NULL COMMENT '考试ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `question_score` int NOT NULL COMMENT '题目在此考试中的分值',
  `sort_order` int NOT NULL DEFAULT 1 COMMENT '题目顺序',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_exam_question`(`exam_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_exam_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_exam_question_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`exam_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_exam_question_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 223 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试题目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_question
-- ----------------------------
INSERT INTO `exam_question` VALUES (1, 1, 1, 10, 1);
INSERT INTO `exam_question` VALUES (2, 1, 2, 20, 2);
INSERT INTO `exam_question` VALUES (3, 1, 3, 10, 3);
INSERT INTO `exam_question` VALUES (4, 1, 4, 20, 4);
INSERT INTO `exam_question` VALUES (5, 1, 5, 40, 5);
INSERT INTO `exam_question` VALUES (6, 10, 101, 5, 1);
INSERT INTO `exam_question` VALUES (7, 10, 102, 5, 2);
INSERT INTO `exam_question` VALUES (8, 10, 103, 5, 3);
INSERT INTO `exam_question` VALUES (9, 10, 104, 10, 4);
INSERT INTO `exam_question` VALUES (10, 10, 105, 10, 5);
INSERT INTO `exam_question` VALUES (11, 10, 106, 5, 6);
INSERT INTO `exam_question` VALUES (12, 10, 107, 5, 7);
INSERT INTO `exam_question` VALUES (13, 10, 108, 5, 8);
INSERT INTO `exam_question` VALUES (14, 10, 109, 10, 9);
INSERT INTO `exam_question` VALUES (15, 10, 110, 10, 10);
INSERT INTO `exam_question` VALUES (16, 10, 111, 10, 11);
INSERT INTO `exam_question` VALUES (17, 10, 112, 15, 12);
INSERT INTO `exam_question` VALUES (18, 10, 113, 15, 13);
INSERT INTO `exam_question` VALUES (19, 11, 101, 5, 1);
INSERT INTO `exam_question` VALUES (20, 11, 102, 5, 2);
INSERT INTO `exam_question` VALUES (21, 11, 103, 5, 3);
INSERT INTO `exam_question` VALUES (22, 11, 104, 10, 4);
INSERT INTO `exam_question` VALUES (23, 11, 105, 10, 5);
INSERT INTO `exam_question` VALUES (24, 11, 106, 5, 6);
INSERT INTO `exam_question` VALUES (25, 11, 107, 5, 7);
INSERT INTO `exam_question` VALUES (26, 11, 108, 5, 8);
INSERT INTO `exam_question` VALUES (174, 200, 401, 5, 1);
INSERT INTO `exam_question` VALUES (175, 200, 402, 5, 2);
INSERT INTO `exam_question` VALUES (176, 200, 403, 5, 3);
INSERT INTO `exam_question` VALUES (177, 200, 404, 10, 4);
INSERT INTO `exam_question` VALUES (178, 200, 406, 5, 5);
INSERT INTO `exam_question` VALUES (179, 200, 408, 10, 6);
INSERT INTO `exam_question` VALUES (180, 200, 411, 5, 7);
INSERT INTO `exam_question` VALUES (181, 200, 412, 5, 8);
INSERT INTO `exam_question` VALUES (182, 200, 413, 10, 9);
INSERT INTO `exam_question` VALUES (183, 200, 414, 10, 10);
INSERT INTO `exam_question` VALUES (184, 200, 410, 15, 11);
INSERT INTO `exam_question` VALUES (185, 200, 415, 10, 12);
INSERT INTO `exam_question` VALUES (186, 201, 401, 4, 1);
INSERT INTO `exam_question` VALUES (187, 201, 402, 4, 2);
INSERT INTO `exam_question` VALUES (188, 201, 411, 4, 3);
INSERT INTO `exam_question` VALUES (189, 201, 416, 4, 4);
INSERT INTO `exam_question` VALUES (190, 201, 417, 4, 5);
INSERT INTO `exam_question` VALUES (191, 201, 404, 8, 6);
INSERT INTO `exam_question` VALUES (192, 201, 405, 8, 7);
INSERT INTO `exam_question` VALUES (193, 201, 413, 8, 8);
INSERT INTO `exam_question` VALUES (194, 201, 406, 4, 9);
INSERT INTO `exam_question` VALUES (195, 201, 407, 4, 10);
INSERT INTO `exam_question` VALUES (196, 201, 408, 8, 11);
INSERT INTO `exam_question` VALUES (197, 201, 409, 8, 12);
INSERT INTO `exam_question` VALUES (198, 201, 414, 8, 13);
INSERT INTO `exam_question` VALUES (199, 201, 418, 8, 14);
INSERT INTO `exam_question` VALUES (200, 201, 419, 8, 15);
INSERT INTO `exam_question` VALUES (201, 201, 410, 12, 16);
INSERT INTO `exam_question` VALUES (202, 201, 420, 12, 17);
INSERT INTO `exam_question` VALUES (203, 202, 401, 5, 1);
INSERT INTO `exam_question` VALUES (204, 202, 402, 5, 2);
INSERT INTO `exam_question` VALUES (205, 202, 403, 5, 3);
INSERT INTO `exam_question` VALUES (206, 202, 404, 10, 4);
INSERT INTO `exam_question` VALUES (207, 202, 405, 10, 5);
INSERT INTO `exam_question` VALUES (208, 202, 406, 5, 6);
INSERT INTO `exam_question` VALUES (209, 202, 407, 5, 7);
INSERT INTO `exam_question` VALUES (210, 202, 408, 5, 8);
INSERT INTO `exam_question` VALUES (211, 203, 411, 5, 1);
INSERT INTO `exam_question` VALUES (212, 203, 412, 5, 2);
INSERT INTO `exam_question` VALUES (213, 203, 413, 10, 3);
INSERT INTO `exam_question` VALUES (214, 203, 406, 5, 4);
INSERT INTO `exam_question` VALUES (215, 203, 409, 10, 5);
INSERT INTO `exam_question` VALUES (216, 203, 414, 10, 6);
INSERT INTO `exam_question` VALUES (217, 203, 415, 5, 7);
INSERT INTO `exam_question` VALUES (218, 204, 416, 5, 1);
INSERT INTO `exam_question` VALUES (219, 204, 417, 5, 2);
INSERT INTO `exam_question` VALUES (220, 204, 418, 10, 3);
INSERT INTO `exam_question` VALUES (221, 204, 419, 10, 4);
INSERT INTO `exam_question` VALUES (222, 204, 420, 15, 5);

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `question_id` int NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `question_type` tinyint NOT NULL COMMENT '题目类型：1单选，2多选，3判断，4填空，5简答',
  `question_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容',
  `question_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '题目图片URL',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '正确答案（JSON格式）',
  `question_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '题目解析',
  `difficulty_level` tinyint NOT NULL DEFAULT 1 COMMENT '难度等级：1简单，2中等，3困难',
  `question_score` int NOT NULL DEFAULT 5 COMMENT '题目分值',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`question_id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_question_type`(`question_type` ASC) USING BTREE,
  CONSTRAINT `fk_question_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 421 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, 1, 1, 'Java是哪种类型的编程语言？', NULL, '[\"A\"]', 'Java是面向对象的编程语言，具有跨平台特性。', 1, 5, '2026-01-05 21:14:33', '2026-01-05 21:14:33', 0);
INSERT INTO `question` VALUES (2, 1, 2, '以下哪些是Java的特点？（多选）', NULL, '[\"A\",\"B\",\"C\"]', 'Java具有面向对象、跨平台、安全性等特点。', 2, 10, '2026-01-05 21:14:33', '2026-01-05 21:14:33', 0);
INSERT INTO `question` VALUES (3, 1, 3, 'Java程序需要编译后才能运行。', NULL, 'true', 'Java源代码需要先编译成字节码，然后在JVM上运行。', 1, 5, '2026-01-05 21:14:33', '2026-01-05 21:14:33', 0);
INSERT INTO `question` VALUES (4, 1, 4, 'Java中用于声明变量的关键字是____。', NULL, 'int,String,double,float,boolean,char,byte,short,long', '根据数据类型不同，可以使用不同的关键字声明变量。', 2, 10, '2026-01-05 21:14:33', '2026-01-06 23:16:16', 1);
INSERT INTO `question` VALUES (5, 1, 5, '请简述Java面向对象编程的三大特性。', NULL, '', '封装、继承、多态是面向对象编程的三大特性。', 3, 15, '2026-01-05 21:14:33', '2026-01-06 23:16:19', 1);
INSERT INTO `question` VALUES (101, 1, 1, 'Java是由哪家公司开发的？', NULL, '[\"B\"]', 'Java最初由Sun Microsystems公司开发，后被Oracle收购。', 1, 5, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (102, 1, 1, '下列哪个关键字用于定义Java类？', NULL, '[\"A\"]', 'class关键字用于定义Java类。', 1, 5, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (103, 1, 1, 'Java程序的入口方法是？', NULL, '[\"C\"]', 'main方法是Java程序的入口点。', 2, 5, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (104, 1, 2, '以下哪些是Java的基本数据类型？（多选）', NULL, '[\"A\",\"B\",\"D\"]', 'Java的基本数据类型包括：byte、short、int、long、float、double、char、boolean。', 2, 10, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (105, 1, 2, 'Java面向对象编程的特性包括？（多选）', NULL, '[\"A\",\"B\",\"C\"]', '面向对象编程的三大特性：封装、继承、多态。', 2, 10, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (106, 1, 3, 'Java是一种编译型语言。', NULL, '\"false\"', 'Java是一种解释型语言，源代码先编译成字节码，然后由JVM解释执行。', 2, 5, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (107, 1, 3, 'Java支持多重继承。', NULL, '\"false\"', 'Java不支持类的多重继承，但支持接口的多重继承。', 2, 5, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (108, 1, 3, 'Java程序具有跨平台特性。', NULL, '\"true\"', 'Java程序编译后生成字节码，可以在任何安装了JVM的平台上运行。', 1, 5, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (109, 1, 4, 'Java中声明一个整型变量的关键字是____，声明一个字符串变量的类型是____。', NULL, '\"int,String\"', '整型变量使用int关键字声明，字符串变量使用String类型。', 1, 10, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (110, 1, 4, 'Java中的访问修饰符有四种：____、____、____、____。', NULL, '\"public,private,protected,default\"', 'Java的四种访问修饰符：public（公共）、private（私有）、protected（受保护）、default（默认/包访问）。', 2, 10, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (111, 1, 4, 'Java中创建对象使用____关键字，销毁对象由____自动完成。', NULL, '\"new,垃圾回收器\"', '使用new关键字创建对象，垃圾回收器（GC）自动回收不再使用的对象。', 2, 10, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (112, 1, 5, '请简述Java面向对象编程的三大特性，并举例说明。', NULL, '\"封装：将数据和操作数据的方法封装在类中，如private变量配合getter/setter方法。继承：子类继承父类的属性和方法，如class Student extends Person。多态：同一个方法在不同对象中有不同的实现，如方法重写和接口实现。\"', '这是面向对象编程的核心概念，需要理解每个特性的含义和应用场景。', 3, 15, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (113, 1, 5, '解释Java中的异常处理机制，包括try-catch-finally的作用。', NULL, '\"Java异常处理机制：try块包含可能出现异常的代码；catch块捕获并处理特定类型的异常；finally块无论是否发生异常都会执行，通常用于资源清理。异常分为检查异常和运行时异常，检查异常必须处理或声明抛出。\"', '异常处理是Java程序健壮性的重要保证，需要掌握正确的使用方法。', 3, 15, '2026-01-07 10:37:44', '2026-01-07 10:37:44', 0);
INSERT INTO `question` VALUES (401, 100, 1, '函数f(x) = √(x-1)的定义域是？', NULL, '[\"C\"]', '根式函数要求被开方数非负，所以x-1≥0，即x≥1。', 1, 5, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (402, 100, 1, '极限lim(x→0) (sin x)/x 的值是？', NULL, '[\"B\"]', '这是重要极限，lim(x→0) (sin x)/x = 1。', 2, 5, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (403, 100, 1, '函数f(x) = x² - 2x + 1在区间[0,3]上的最小值是？', NULL, '[\"A\"]', 'f(x) = (x-1)²，在x=1处取得最小值0。', 2, 5, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (404, 100, 2, '以下哪些函数是偶函数？', NULL, '[\"A\",\"C\"]', '偶函数满足f(-x) = f(x)，cos x和x²都是偶函数。', 2, 10, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (405, 100, 2, '关于函数极限，以下说法正确的是？', NULL, '[\"B\",\"D\"]', '极限存在不要求函数在该点有定义，且左右极限相等是极限存在的充要条件。', 3, 10, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (406, 100, 3, '连续函数一定可导。', NULL, '\"false\"', '连续是可导的必要条件但不是充分条件，如f(x)=|x|在x=0处连续但不可导。', 2, 5, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (407, 100, 3, '如果函数在某点的左右极限都存在且相等，则函数在该点连续。', NULL, '\"false\"', '还需要函数在该点有定义且函数值等于极限值。', 2, 5, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (408, 100, 4, '极限lim(x→∞) (3x²+2x+1)/(x²+x+2) = ____。', NULL, '\"3\"', '分子分母同时除以x²的最高次项，得到3。', 2, 10, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (409, 100, 4, '函数f(x) = x³ - 3x的导数f\'(x) = ____。', NULL, '\"3x²-3\"', '根据幂函数求导法则，(x³)\' = 3x²，常数的导数为0。', 1, 10, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (410, 100, 5, '用定义证明：lim(x→2) (3x-1) = 5。', NULL, '\"根据极限的ε-δ定义，对任意ε>0，要使|3x-1-5|<ε，即|3x-6|<ε，即|x-2|<ε/3。取δ=ε/3，当0<|x-2|<δ时，就有|3x-1-5|<ε，所以极限为5。\"', '这是用极限定义证明的标准方法。', 3, 15, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (411, 100, 1, '函数f(x) = e^(2x)的导数是？', NULL, '[\"B\"]', '复合函数求导，(e^(2x))\' = e^(2x) · (2x)\' = 2e^(2x)。', 2, 5, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (412, 100, 1, '曲线y = x³在点(1,1)处的切线斜率是？', NULL, '[\"C\"]', 'y\' = 3x²，在x=1处，切线斜率为3。', 1, 5, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (413, 100, 2, '以下哪些求导法则是正确的？', NULL, '[\"A\",\"B\",\"D\"]', '积的导数法则、商的导数法则和复合函数求导法则都是正确的。', 2, 10, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (414, 100, 4, '函数y = ln(x²+1)的导数dy/dx = ____。', NULL, '\"2x/(x²+1)\"', '复合函数求导：(ln u)\' = u\'/u，这里u = x²+1。', 2, 10, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (415, 100, 4, '函数f(x) = x·sin x的导数f\'(x) = ____。', NULL, '\"sin x + x·cos x\"', '使用积的导数法则：(uv)\' = u\'v + uv\'。', 2, 10, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (416, 100, 1, '∫x²dx = ？', NULL, '[\"A\"]', '根据幂函数积分公式：∫x^n dx = x^(n+1)/(n+1) + C。', 1, 5, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (417, 100, 1, '定积分∫₀¹ x dx的值是？', NULL, '[\"B\"]', '∫₀¹ x dx = [x²/2]₀¹ = 1/2 - 0 = 1/2。', 1, 5, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (418, 100, 4, '∫(2x+3)dx = ____。', NULL, '\"x²+3x+C\"', '分别对每一项积分：∫2x dx + ∫3 dx = x² + 3x + C。', 1, 10, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (419, 100, 4, '∫₀^π sin x dx = ____。', NULL, '\"2\"', '∫₀^π sin x dx = [-cos x]₀^π = -cos π + cos 0 = 1 + 1 = 2。', 2, 10, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);
INSERT INTO `question` VALUES (420, 100, 5, '计算不定积分∫x·e^x dx，并说明所用方法。', NULL, '\"使用分部积分法：设u=x，dv=e^x dx，则du=dx，v=e^x。∫x·e^x dx = x·e^x - ∫e^x dx = x·e^x - e^x + C = e^x(x-1) + C。\"', '分部积分法是处理两个不同类型函数乘积积分的重要方法。', 3, 15, '2026-01-07 10:48:38', '2026-01-07 10:48:38', 0);

-- ----------------------------
-- Table structure for question_option
-- ----------------------------
DROP TABLE IF EXISTS `question_option`;
CREATE TABLE `question_option`  (
  `option_id` int NOT NULL AUTO_INCREMENT COMMENT '选项ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `option_label` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项标签（A、B、C、D）',
  `option_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项内容',
  `option_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '选项图片URL',
  `is_correct` tinyint NOT NULL DEFAULT 0 COMMENT '是否正确答案：0否，1是',
  `sort_order` int NOT NULL DEFAULT 1 COMMENT '排序',
  PRIMARY KEY (`option_id`) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_option_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 541 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '选择题选项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_option
-- ----------------------------
INSERT INTO `question_option` VALUES (1, 1, 'A', '面向对象编程语言', NULL, 1, 1);
INSERT INTO `question_option` VALUES (2, 1, 'B', '函数式编程语言', NULL, 0, 2);
INSERT INTO `question_option` VALUES (3, 1, 'C', '过程式编程语言', NULL, 0, 3);
INSERT INTO `question_option` VALUES (4, 1, 'D', '脚本编程语言', NULL, 0, 4);
INSERT INTO `question_option` VALUES (5, 2, 'A', '面向对象', NULL, 1, 1);
INSERT INTO `question_option` VALUES (6, 2, 'B', '跨平台', NULL, 1, 2);
INSERT INTO `question_option` VALUES (7, 2, 'C', '安全性高', NULL, 1, 3);
INSERT INTO `question_option` VALUES (8, 2, 'D', '运行速度最快', NULL, 0, 4);
INSERT INTO `question_option` VALUES (201, 101, 'A', 'Microsoft', NULL, 0, 1);
INSERT INTO `question_option` VALUES (202, 101, 'B', 'Sun Microsystems', NULL, 1, 2);
INSERT INTO `question_option` VALUES (203, 101, 'C', 'Google', NULL, 0, 3);
INSERT INTO `question_option` VALUES (204, 101, 'D', 'Apple', NULL, 0, 4);
INSERT INTO `question_option` VALUES (205, 102, 'A', 'class', NULL, 1, 1);
INSERT INTO `question_option` VALUES (206, 102, 'B', 'interface', NULL, 0, 2);
INSERT INTO `question_option` VALUES (207, 102, 'C', 'extends', NULL, 0, 3);
INSERT INTO `question_option` VALUES (208, 102, 'D', 'implements', NULL, 0, 4);
INSERT INTO `question_option` VALUES (209, 103, 'A', 'start()', NULL, 0, 1);
INSERT INTO `question_option` VALUES (210, 103, 'B', 'run()', NULL, 0, 2);
INSERT INTO `question_option` VALUES (211, 103, 'C', 'main()', NULL, 1, 3);
INSERT INTO `question_option` VALUES (212, 103, 'D', 'init()', NULL, 0, 4);
INSERT INTO `question_option` VALUES (213, 104, 'A', 'int', NULL, 1, 1);
INSERT INTO `question_option` VALUES (214, 104, 'B', 'double', NULL, 1, 2);
INSERT INTO `question_option` VALUES (215, 104, 'C', 'String', NULL, 0, 3);
INSERT INTO `question_option` VALUES (216, 104, 'D', 'boolean', NULL, 1, 4);
INSERT INTO `question_option` VALUES (217, 105, 'A', '封装', NULL, 1, 1);
INSERT INTO `question_option` VALUES (218, 105, 'B', '继承', NULL, 1, 2);
INSERT INTO `question_option` VALUES (219, 105, 'C', '多态', NULL, 1, 3);
INSERT INTO `question_option` VALUES (220, 105, 'D', '抽象', NULL, 0, 4);
INSERT INTO `question_option` VALUES (501, 401, 'A', '(-∞,+∞)', NULL, 0, 1);
INSERT INTO `question_option` VALUES (502, 401, 'B', '(1,+∞)', NULL, 0, 2);
INSERT INTO `question_option` VALUES (503, 401, 'C', '[1,+∞)', NULL, 1, 3);
INSERT INTO `question_option` VALUES (504, 401, 'D', '(0,+∞)', NULL, 0, 4);
INSERT INTO `question_option` VALUES (505, 402, 'A', '0', NULL, 0, 1);
INSERT INTO `question_option` VALUES (506, 402, 'B', '1', NULL, 1, 2);
INSERT INTO `question_option` VALUES (507, 402, 'C', '∞', NULL, 0, 3);
INSERT INTO `question_option` VALUES (508, 402, 'D', '不存在', NULL, 0, 4);
INSERT INTO `question_option` VALUES (509, 403, 'A', '0', NULL, 1, 1);
INSERT INTO `question_option` VALUES (510, 403, 'B', '1', NULL, 0, 2);
INSERT INTO `question_option` VALUES (511, 403, 'C', '2', NULL, 0, 3);
INSERT INTO `question_option` VALUES (512, 403, 'D', '8', NULL, 0, 4);
INSERT INTO `question_option` VALUES (513, 411, 'A', 'e^(2x)', NULL, 0, 1);
INSERT INTO `question_option` VALUES (514, 411, 'B', '2e^(2x)', NULL, 1, 2);
INSERT INTO `question_option` VALUES (515, 411, 'C', '2xe^(2x)', NULL, 0, 3);
INSERT INTO `question_option` VALUES (516, 411, 'D', 'xe^(2x)', NULL, 0, 4);
INSERT INTO `question_option` VALUES (517, 412, 'A', '1', NULL, 0, 1);
INSERT INTO `question_option` VALUES (518, 412, 'B', '2', NULL, 0, 2);
INSERT INTO `question_option` VALUES (519, 412, 'C', '3', NULL, 1, 3);
INSERT INTO `question_option` VALUES (520, 412, 'D', '6', NULL, 0, 4);
INSERT INTO `question_option` VALUES (521, 416, 'A', 'x³/3 + C', NULL, 1, 1);
INSERT INTO `question_option` VALUES (522, 416, 'B', 'x³ + C', NULL, 0, 2);
INSERT INTO `question_option` VALUES (523, 416, 'C', '3x + C', NULL, 0, 3);
INSERT INTO `question_option` VALUES (524, 416, 'D', '2x + C', NULL, 0, 4);
INSERT INTO `question_option` VALUES (525, 417, 'A', '1', NULL, 0, 1);
INSERT INTO `question_option` VALUES (526, 417, 'B', '1/2', NULL, 1, 2);
INSERT INTO `question_option` VALUES (527, 417, 'C', '2', NULL, 0, 3);
INSERT INTO `question_option` VALUES (528, 417, 'D', '0', NULL, 0, 4);
INSERT INTO `question_option` VALUES (529, 404, 'A', 'f(x) = cos x', NULL, 1, 1);
INSERT INTO `question_option` VALUES (530, 404, 'B', 'f(x) = sin x', NULL, 0, 2);
INSERT INTO `question_option` VALUES (531, 404, 'C', 'f(x) = x²', NULL, 1, 3);
INSERT INTO `question_option` VALUES (532, 404, 'D', 'f(x) = x³', NULL, 0, 4);
INSERT INTO `question_option` VALUES (533, 405, 'A', '函数在某点有极限，则函数在该点必须有定义', NULL, 0, 1);
INSERT INTO `question_option` VALUES (534, 405, 'B', '函数在某点的左右极限都存在且相等，则极限存在', NULL, 1, 2);
INSERT INTO `question_option` VALUES (535, 405, 'C', '极限存在则函数连续', NULL, 0, 3);
INSERT INTO `question_option` VALUES (536, 405, 'D', '极限的四则运算法则适用于极限存在的情况', NULL, 1, 4);
INSERT INTO `question_option` VALUES (537, 413, 'A', '(uv)\' = u\'v + uv\'', NULL, 1, 1);
INSERT INTO `question_option` VALUES (538, 413, 'B', '(u/v)\' = (u\'v - uv\')/v²', NULL, 1, 2);
INSERT INTO `question_option` VALUES (539, 413, 'C', '(u+v)\' = u\' + v\'', NULL, 0, 3);
INSERT INTO `question_option` VALUES (540, 413, 'D', '[f(g(x))]\' = f\'(g(x))·g\'(x)', NULL, 1, 4);

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `stu_id` int NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `stu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生名称',
  `stu_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生账户',
  `stu_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生密码',
  `stu_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生电话',
  `stu_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生邮箱',
  `stu_university` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生大学',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `del` int NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`stu_id`) USING BTREE,
  UNIQUE INDEX `uk_student_account`(`stu_account` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES (1, '张三', '13579', '24680', '1213', '761542@qq.com', '北京', '/uploads/images/3768c345-ef63-45fa-a9f0-3fb62e7cd377.jpeg', 0);
INSERT INTO `student` VALUES (2, '李四', '12345', '67890', '12453472342', '14142342@342.com', '阿贝多', NULL, 0);
INSERT INTO `student` VALUES (3, '张明宇', 'stu_2025001', 'Stu@123456', '13911112001', 'zhangmy@student.zhjypt.com', '北京市朝阳区建国路88号', NULL, 0);
INSERT INTO `student` VALUES (4, '李雨桐', 'stu_2025002', 'Stu@123456', '13911112002', 'liyutong@student.zhjypt.com', '上海市静安区南京西路178号', NULL, 0);
INSERT INTO `student` VALUES (5, '王浩然', 'stu_2025003', 'Stu@123456', '13911112003', 'wanghr@student.zhjypt.com', '广州市天河区天河路385号', NULL, 0);
INSERT INTO `student` VALUES (6, '刘思琪', 'stu_2025004', 'Stu@123456', '13911112004', 'liusiqi@student.zhjypt.com', '深圳市福田区深南大道7008号', NULL, 0);
INSERT INTO `student` VALUES (7, '陈俊浩', 'stu_2025005', 'Stu@123456', '13911112005', 'chenjh@student.zhjypt.com', '杭州市滨江区江南大道100号', NULL, 0);
INSERT INTO `student` VALUES (8, '杨雨欣', 'stu_2025006', 'Stu@123456', '13911112006', 'yangyx@student.zhjypt.com', '南京市秦淮区夫子庙平江府路30号', NULL, 0);
INSERT INTO `student` VALUES (9, '黄梓轩', 'stu_2025007', 'Stu@123456', '13911112007', 'huangzx@student.zhjypt.com', '成都市武侯区武侯祠大街231号', NULL, 0);
INSERT INTO `student` VALUES (10, '吴语桐', 'stu_2025008', 'Stu@123456', '13911112008', 'wuyutong@student.zhjypt.com', '武汉市洪山区光谷大道709号', NULL, 0);
INSERT INTO `student` VALUES (11, '郑浩然', 'stu_2025009', 'Stu@123456', '13911112009', 'zhenghr@student.zhjypt.com', '西安市雁塔区大雁塔南广场1号', NULL, 0);
INSERT INTO `student` VALUES (12, '马思远', 'stu_2025010', 'Stu@123456', '13911112010', 'masiyuan@student.zhjypt.com', '重庆市渝中区解放碑邹容路100号', NULL, 0);
INSERT INTO `student` VALUES (13, '朱雨辰', 'stu_2025011', 'Stu@123456', '13911112011', 'zhuyc@student.zhjypt.com', '天津市和平区滨江道200号', NULL, 0);
INSERT INTO `student` VALUES (14, '胡艺涵', 'stu_2025012', 'Stu@123456', '13911112012', 'huyihan@student.zhjypt.com', '苏州市姑苏区观前街120号', NULL, 0);
INSERT INTO `student` VALUES (15, '林梓豪', 'stu_2025013', 'Stu@123456', '13911112013', 'linzh@student.zhjypt.com', '长沙市岳麓区橘子洲头2号', NULL, 0);
INSERT INTO `student` VALUES (16, '罗思雅', 'stu_2025014', 'Stu@123456', '13911112014', 'luosiya@student.zhjypt.com', '郑州市金水区二七路150号', NULL, 0);
INSERT INTO `student` VALUES (17, '周俊逸', 'stu_2025015', 'Stu@123456', '13911112015', 'zhoujy@student.zhjypt.com', '青岛市市南区五四广场6号', NULL, 0);
INSERT INTO `student` VALUES (18, '徐若曦', 'stu_2025016', 'Stu@123456', '13911112016', 'xuruoxi@student.zhjypt.com', '沈阳市和平区青年大街386号', NULL, 0);
INSERT INTO `student` VALUES (19, '孙浩然', 'stu_2025017', 'Stu@123456', '13911112017', 'sunhr@student.zhjypt.com', '长春市朝阳区人民大街500号', NULL, 0);
INSERT INTO `student` VALUES (20, '韩雨桐', 'stu_2025018', 'Stu@123456', '13911112018', 'hanyutong@student.zhjypt.com', '哈尔滨市南岗区中山路251号', NULL, 0);
INSERT INTO `student` VALUES (21, '000grbwr', '135799', '24680', '', '', '', NULL, 0);

-- ----------------------------
-- Table structure for student_answer
-- ----------------------------
DROP TABLE IF EXISTS `student_answer`;
CREATE TABLE `student_answer`  (
  `answer_id` int NOT NULL AUTO_INCREMENT COMMENT '答题记录ID',
  `record_id` int NOT NULL COMMENT '考试记录ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `student_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学生答案（JSON格式）',
  `is_correct` tinyint NULL DEFAULT NULL COMMENT '是否正确：0错误，1正确，NULL未评分',
  `question_score` int NOT NULL COMMENT '题目分值',
  `student_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '学生得分',
  `answer_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
  PRIMARY KEY (`answer_id`) USING BTREE,
  UNIQUE INDEX `uk_record_question`(`record_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_record_id`(`record_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_answer_record` FOREIGN KEY (`record_id`) REFERENCES `student_exam_record` (`record_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 142 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生答题记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_answer
-- ----------------------------

-- ----------------------------
-- Table structure for student_content_progress
-- ----------------------------
DROP TABLE IF EXISTS `student_content_progress`;
CREATE TABLE `student_content_progress`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_id` int NOT NULL COMMENT '学生ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `chapter_id` int NOT NULL COMMENT '章节ID',
  `content_id` int NOT NULL COMMENT '内容ID',
  `is_completed` tinyint NOT NULL DEFAULT 0 COMMENT '是否完成：0未完成，1已完成',
  `watch_progress` int NOT NULL DEFAULT 0 COMMENT '观看进度百分比（0-100）',
  `first_study_time` datetime NULL DEFAULT NULL COMMENT '首次学习时间',
  `completed_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_content`(`student_id` ASC, `content_id` ASC) USING BTREE,
  INDEX `idx_student_course`(`student_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_content`(`content_id` ASC) USING BTREE,
  INDEX `idx_chapter`(`chapter_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生内容学习进度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_content_progress
-- ----------------------------
INSERT INTO `student_content_progress` VALUES (1, 1, 1, 1, 12, 1, 100, '2025-12-29 17:41:47', '2025-12-29 17:41:55', '2025-12-29 17:54:32');
INSERT INTO `student_content_progress` VALUES (2, 1, 1, 1, 13, 1, 100, '2025-12-29 17:41:57', '2025-12-29 17:41:57', '2025-12-29 17:41:57');
INSERT INTO `student_content_progress` VALUES (3, 1, 1, 1, 1, 1, 100, '2025-12-29 17:42:05', '2025-12-29 17:42:14', '2025-12-29 17:42:16');
INSERT INTO `student_content_progress` VALUES (4, 1, 1, 1, 3, 1, 100, '2025-12-29 17:42:16', '2025-12-29 17:42:16', '2025-12-29 17:42:16');
INSERT INTO `student_content_progress` VALUES (5, 1, 1, 1, 15, 1, 100, '2025-12-29 17:57:38', '2025-12-29 17:57:38', '2025-12-29 17:57:38');
INSERT INTO `student_content_progress` VALUES (6, 1, 100, 201, 301, 1, 100, '2026-01-07 11:14:08', '2026-01-07 11:25:20', '2026-01-07 11:25:20');
INSERT INTO `student_content_progress` VALUES (7, 1, 100, 201, 302, 1, 100, '2026-01-09 19:08:41', '2026-01-09 19:08:41', '2026-01-09 19:08:41');
INSERT INTO `student_content_progress` VALUES (8, 1, 1, 2, 16, 1, 100, '2026-01-25 13:03:52', '2026-01-25 13:04:30', '2026-01-25 13:04:30');
INSERT INTO `student_content_progress` VALUES (9, 1, 1, 2, 6, 1, 100, '2026-01-25 13:05:47', '2026-01-25 13:05:47', '2026-01-25 13:05:47');
INSERT INTO `student_content_progress` VALUES (11, 1, 1, 2, 5, 1, 100, '2026-03-03 16:28:08', '2026-03-03 17:23:49', '2026-03-03 17:23:49');

-- ----------------------------
-- Table structure for student_course
-- ----------------------------
DROP TABLE IF EXISTS `student_course`;
CREATE TABLE `student_course`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_id` int NOT NULL COMMENT '学生ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `enroll_time` datetime NOT NULL COMMENT '选课时间',
  `progress` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '学习进度百分比',
  `completed_contents` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '已完成的内容ID列表（JSON格式）',
  `last_study_time` datetime NULL DEFAULT NULL COMMENT '最后学习时间',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0退课，1正常',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_course`(`student_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 240 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生选课表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_course
-- ----------------------------
INSERT INTO `student_course` VALUES (1, 1, 1, '2025-12-27 16:51:50', 90.38, NULL, '2026-03-03 17:23:49', 1);
INSERT INTO `student_course` VALUES (2, 1, 2, '2025-12-29 16:04:07', 0.00, NULL, NULL, 0);
INSERT INTO `student_course` VALUES (11, 1, 3, '2025-12-29 17:10:14', 0.00, NULL, NULL, 0);
INSERT INTO `student_course` VALUES (12, 1, 4, '2025-12-29 17:10:18', 0.00, NULL, NULL, 0);
INSERT INTO `student_course` VALUES (13, 1, 100, '2026-01-07 10:49:15', 5.51, NULL, '2026-01-09 19:08:41', 1);
INSERT INTO `student_course` VALUES (14, 21, 100, '2026-01-12 14:45:00', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (15, 21, 1, '2026-01-12 14:45:11', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (16, 1, 121, '2026-03-03 14:41:38', 0.00, NULL, NULL, 0);
INSERT INTO `student_course` VALUES (80, 20, 1, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (81, 19, 1, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (82, 18, 1, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (83, 10, 1, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (84, 7, 1, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (85, 5, 1, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (86, 4, 1, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (87, 20, 2, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (88, 17, 2, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (89, 14, 2, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (90, 13, 2, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (91, 12, 2, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (92, 11, 2, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (93, 9, 2, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (94, 5, 2, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (95, 12, 3, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (96, 11, 3, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (97, 7, 3, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (98, 4, 3, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (99, 19, 4, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (100, 18, 4, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (101, 7, 4, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (102, 4, 4, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (103, 17, 5, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (104, 16, 5, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (105, 12, 5, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (106, 10, 5, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (107, 5, 5, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (108, 14, 100, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (109, 6, 100, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (110, 4, 100, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (111, 3, 100, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (112, 13, 101, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (113, 12, 101, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (114, 6, 101, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (115, 3, 101, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (116, 2, 101, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (117, 19, 102, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (118, 14, 102, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (119, 13, 102, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (120, 6, 102, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (121, 3, 102, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (122, 21, 103, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (123, 15, 103, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (124, 14, 103, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (125, 20, 104, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (126, 19, 104, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (127, 18, 104, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (128, 8, 104, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (129, 7, 104, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (130, 6, 104, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (131, 20, 105, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (132, 18, 105, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (133, 15, 105, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (134, 10, 105, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (135, 9, 105, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (136, 5, 105, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (137, 3, 105, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (138, 17, 106, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (139, 14, 106, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (140, 12, 106, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (141, 11, 106, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (142, 8, 106, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (143, 5, 106, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (144, 20, 107, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (145, 18, 107, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (146, 11, 107, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (147, 10, 107, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (148, 8, 107, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (149, 3, 107, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (150, 13, 108, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (151, 8, 108, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (152, 6, 108, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (153, 20, 109, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (154, 15, 109, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (155, 11, 109, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (156, 4, 109, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (157, 1, 109, '2026-03-02 17:24:37', 0.00, NULL, NULL, 0);
INSERT INTO `student_course` VALUES (158, 15, 110, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (159, 12, 110, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (160, 4, 110, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (161, 1, 110, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (162, 20, 111, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (163, 18, 111, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (164, 17, 111, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (165, 13, 111, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (166, 10, 111, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (167, 9, 111, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (168, 8, 111, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (169, 7, 111, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (170, 5, 111, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (171, 18, 112, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (172, 16, 112, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (173, 15, 112, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (174, 12, 112, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (175, 10, 112, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (176, 9, 112, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (177, 8, 112, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (178, 2, 112, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (179, 21, 113, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (180, 20, 113, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (181, 15, 113, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (182, 12, 113, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (183, 11, 113, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (184, 5, 113, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (185, 1, 113, '2026-03-02 17:24:37', 0.00, NULL, NULL, 0);
INSERT INTO `student_course` VALUES (186, 16, 114, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (187, 12, 114, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (188, 10, 114, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (189, 5, 114, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (190, 3, 114, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (191, 21, 115, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (192, 8, 115, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (193, 3, 115, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (194, 21, 116, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (195, 20, 116, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (196, 17, 116, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (197, 14, 116, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (198, 6, 116, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (199, 4, 116, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (200, 2, 116, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (201, 1, 116, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (202, 21, 117, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (203, 19, 117, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (204, 18, 117, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (205, 12, 117, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (206, 6, 117, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (207, 2, 117, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (208, 18, 118, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (209, 12, 118, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (210, 8, 118, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (211, 1, 118, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (212, 21, 119, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (213, 18, 119, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (214, 11, 119, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (215, 4, 119, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (216, 12, 120, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (217, 8, 120, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (218, 4, 120, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (219, 19, 121, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (220, 15, 121, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (221, 12, 121, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (222, 11, 121, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (223, 7, 121, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (224, 5, 121, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (225, 3, 121, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (226, 21, 122, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (227, 19, 122, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (228, 17, 122, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (229, 7, 122, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (230, 21, 123, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (231, 19, 123, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (232, 14, 123, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (233, 9, 123, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (234, 8, 123, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (235, 7, 123, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (236, 6, 123, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (237, 4, 123, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (238, 2, 123, '2026-03-02 17:24:37', 0.00, NULL, NULL, 1);
INSERT INTO `student_course` VALUES (239, 1, 122, '2026-03-03 15:06:43', 0.00, NULL, NULL, 0);

-- ----------------------------
-- Table structure for student_exam_record
-- ----------------------------
DROP TABLE IF EXISTS `student_exam_record`;
CREATE TABLE `student_exam_record`  (
  `record_id` int NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_id` int NOT NULL COMMENT '学生ID',
  `exam_id` int NOT NULL COMMENT '考试ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `total_score` int NOT NULL COMMENT '总分',
  `student_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '学生得分',
  `pass_status` tinyint NULL DEFAULT NULL COMMENT '通过状态：0未通过，1通过',
  `exam_status` tinyint NOT NULL DEFAULT 0 COMMENT '考试状态：0进行中，1已提交，2已评分',
  `attempt_number` int NOT NULL DEFAULT 1 COMMENT '考试次数',
  `time_used` int NULL DEFAULT NULL COMMENT '用时（分钟）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_student_exam`(`student_id` ASC, `exam_id` ASC) USING BTREE,
  INDEX `idx_exam_id`(`exam_id` ASC) USING BTREE,
  CONSTRAINT `fk_record_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`exam_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_record_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`stu_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生考试记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_exam_record
-- ----------------------------

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `teach_id` int NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  `teach_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师名称',
  `teach_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师账户',
  `teach_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师密码',
  `teach_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师电话',
  `teach_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师邮箱',
  `teach_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师地址',
  `teach_subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教学科目',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `del` int NOT NULL DEFAULT 0 COMMENT '逻辑删除，0未删除，1已删除',
  PRIMARY KEY (`teach_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO `teacher` VALUES (1, '何老师', '13579', '24680', '1234214', '345', '3145', NULL, NULL, 0);
INSERT INTO `teacher` VALUES (2, '李老师', 'teach_math_zhou', 'Teach@2025', '13722223001', 'zhou_math@teacher.zhjypt.com', '北京市海淀区清华园1号', '数学', NULL, 0);
INSERT INTO `teacher` VALUES (3, '陈老师', 'teach_chinese_wu', 'Teach@2025', '13722223002', 'wu_chinese@teacher.zhjypt.com', '上海市杨浦区邯郸路220号', '语文', NULL, 0);
INSERT INTO `teacher` VALUES (4, '王老师', 'teach_english_zheng', 'Teach@2025', '13722223003', 'zheng_english@teacher.zhjypt.com', '广州市越秀区新港西路135号', '英语', NULL, 0);
INSERT INTO `teacher` VALUES (5, '杨老师', 'teach_physics_wang', 'Teach@2025', '13722223004', 'wang_physics@teacher.zhjypt.com', '深圳市南山区南海大道3688号', '物理', NULL, 0);
INSERT INTO `teacher` VALUES (6, '赵老师', 'teach_chemistry_liu', 'Teach@2025', '13722223005', 'liu_chemistry@teacher.zhjypt.com', '杭州市西湖区余杭塘路866号', '化学', NULL, 0);
INSERT INTO `teacher` VALUES (7, '吴老师', 'teach_biology_huang', 'Teach@2025', '13722223006', 'huang_biology@teacher.zhjypt.com', '南京市栖霞区仙林大道163号', '生物', NULL, 0);
INSERT INTO `teacher` VALUES (8, '马老师', 'teach_history_zhao', 'Teach@2025', '13722223007', 'zhao_history@teacher.zhjypt.com', '成都市武侯区一环路南一段24号', '历史', NULL, 0);
INSERT INTO `teacher` VALUES (9, '王老师', 'teach_geography_chen', 'Teach@2025', '13722223008', 'chen_geography@teacher.zhjypt.com', '武汉市武昌区珞珈山16号', '地理', NULL, 0);
INSERT INTO `teacher` VALUES (10, '杨老师', 'teach_politics_yang', 'Teach@2025', '13722223009', 'yang_politics@teacher.zhjypt.com', '西安市碑林区太白北路229号', '政治', NULL, 0);
INSERT INTO `teacher` VALUES (11, '周老师', 'teach_pe_sun', 'Teach@2025', '13722223010', 'sun_pe@teacher.zhjypt.com', '重庆市沙坪坝区沙正街174号', '体育', NULL, 0);
INSERT INTO `teacher` VALUES (12, '赵老师', 'teach_music_zhu', 'Teach@2025', '13722223011', 'zhu_music@teacher.zhjypt.com', '天津市南开区卫津路94号', '音乐', NULL, 0);
INSERT INTO `teacher` VALUES (13, '张老师', 'teach_art_hu', 'Teach@2025', '13722223012', 'hu_art@teacher.zhjypt.com', '苏州市工业园区仁爱路199号', '美术', NULL, 0);

SET FOREIGN_KEY_CHECKS = 1;
