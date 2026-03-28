package com.example.zhjypt.controller;

import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
@Api(tags = "文件上传")
@CrossOrigin
public class FileUploadController {

    // 上传目录
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * 从原始文件名解析扩展名（含点，小写）。无文件名、无扩展名时返回 null，避免 substring(-1) 导致 500。
     */
    private String safeExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return null;
        }
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex == -1) {
            return null;
        }
        return originalFilename.substring(dotIndex).toLowerCase();
    }
    
    @ApiOperation("上传视频文件")
    @PostMapping("/video")
    public ResultVO uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResultVO.fail("请选择要上传的文件");
        }

        try {
            // 检查文件类型
            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename();
            String extension = safeExtension(originalFilename);
            if (extension == null) {
                return ResultVO.fail(
                        originalFilename == null || originalFilename.trim().isEmpty()
                                ? "无法获取文件名"
                                : "文件缺少扩展名");
            }
            
            if (!isVideoFile(contentType, extension)) {
                return ResultVO.fail("只能上传视频文件（mp4, avi, mov, wmv, flv, mkv）");
            }

            // 创建上传目录
            File uploadDir = new File(UPLOAD_DIR + "videos/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String filename = UUID.randomUUID().toString() + extension;
            String filePath = UPLOAD_DIR + "videos/" + filename;

            // 保存文件
            Path targetPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), targetPath);

            // 获取文件信息
            File savedFile = new File(filePath);
            long fileSize = savedFile.length();
            
            // 尝试获取视频时长（这里简化处理，实际项目中可以使用FFmpeg等工具）
            Integer duration = getVideoDuration(savedFile);

            // 返回文件信息
            Map<String, Object> result = new HashMap<>();
            result.put("url", "/uploads/videos/" + filename);
            result.put("originalName", originalFilename);
            result.put("fileSize", fileSize);
            result.put("duration", duration);
            result.put("contentType", contentType);
            
            return ResultVO.success("上传成功", result);
            
        } catch (IOException e) {
            e.printStackTrace();
            return ResultVO.fail("文件上传失败：" + e.getMessage());
        }
    }

    @ApiOperation("上传文档文件")
    @PostMapping("/document")
    public ResultVO uploadDocument(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResultVO.fail("请选择要上传的文件");
        }

        try {
            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            String extension = safeExtension(originalFilename);
            if (extension == null) {
                return ResultVO.fail(
                        originalFilename == null || originalFilename.trim().isEmpty()
                                ? "无法获取文件名"
                                : "文件缺少扩展名");
            }
            
            if (!extension.matches("\\.(pdf|doc|docx|ppt|pptx|txt)$")) {
                return ResultVO.fail("只能上传PDF、Word、PowerPoint或文本文件");
            }

            // 创建上传目录
            File uploadDir = new File(UPLOAD_DIR + "documents/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String filename = UUID.randomUUID().toString() + extension;
            String filePath = UPLOAD_DIR + "documents/" + filename;

            // 保存文件
            Path targetPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), targetPath);

            // 获取文件信息
            File savedFile = new File(filePath);
            long fileSize = savedFile.length();

            // 返回文件信息
            Map<String, Object> result = new HashMap<>();
            result.put("url", "/uploads/documents/" + filename);
            result.put("originalName", originalFilename);
            result.put("fileSize", fileSize);
            result.put("contentType", file.getContentType());
            
            return ResultVO.success("上传成功", result);
            
        } catch (IOException e) {
            e.printStackTrace();
            return ResultVO.fail("文件上传失败：" + e.getMessage());
        }
    }

    @ApiOperation("上传图片文件")
    @PostMapping("/image")
    public ResultVO uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResultVO.fail("请选择要上传的文件");
        }

        try {
            // 检查文件类型
            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename();
            String extension = safeExtension(originalFilename);
            if (extension == null) {
                return ResultVO.fail(
                        originalFilename == null || originalFilename.trim().isEmpty()
                                ? "无法获取文件名"
                                : "文件缺少扩展名");
            }
            
            if (!isImageFile(contentType, extension)) {
                return ResultVO.fail("只能上传图片文件（jpg, jpeg, png, gif, bmp）");
            }

            // 创建上传目录
            File uploadDir = new File(UPLOAD_DIR + "images/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String filename = UUID.randomUUID().toString() + extension;
            String filePath = UPLOAD_DIR + "images/" + filename;

            // 保存文件
            Path targetPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), targetPath);

            // 获取文件信息
            File savedFile = new File(filePath);
            long fileSize = savedFile.length();

            // 返回文件信息
            Map<String, Object> result = new HashMap<>();
            result.put("url", "/uploads/images/" + filename);
            result.put("originalName", originalFilename);
            result.put("fileSize", fileSize);
            result.put("contentType", contentType);
            
            return ResultVO.success("上传成功", result);
            
        } catch (IOException e) {
            e.printStackTrace();
            return ResultVO.fail("文件上传失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取远程文件大小")
    @PostMapping("/get-file-size")
    public ResultVO getFileSize(@RequestParam String url) {
        if (url == null || url.trim().isEmpty()) {
            return ResultVO.fail("URL不能为空");
        }

        try {
            // 处理可能的URL格式问题
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            
            java.net.URL fileUrl = new java.net.URL(url);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) fileUrl.openConnection();
            
            // 设置请求头，模拟浏览器访问
            connection.setRequestMethod("HEAD");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestProperty("Accept", "*/*");
            connection.setConnectTimeout(10000); // 10秒连接超时
            connection.setReadTimeout(10000); // 10秒读取超时
            connection.setInstanceFollowRedirects(true); // 允许重定向
            
            try {
                connection.connect();
                int responseCode = connection.getResponseCode();
                
                if (responseCode == 200) {
                    // 获取文件大小
                    long fileSize = connection.getContentLengthLong();
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("fileSize", fileSize > 0 ? fileSize : null);
                    result.put("responseCode", responseCode);
                    
                    return ResultVO.success("获取成功", result);
                } else {
                    return ResultVO.fail("HTTP响应码: " + responseCode);
                }
                
            } finally {
                connection.disconnect();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("获取文件大小失败：" + e.getMessage());
        }
    }

    /**
     * 获取远程文件信息
     */
    private Map<String, Object> getRemoteFileInfo(String url, Integer contentType) throws Exception {
        // 处理可能的URL格式问题
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        
        java.net.URL fileUrl = new java.net.URL(url);
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) fileUrl.openConnection();
        
        // 设置请求头，模拟浏览器访问
        connection.setRequestMethod("HEAD");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        connection.setConnectTimeout(15000); // 15秒连接超时
        connection.setReadTimeout(15000); // 15秒读取超时
        connection.setInstanceFollowRedirects(true); // 允许重定向
        
        try {
            connection.connect();
            int responseCode = connection.getResponseCode();
            
            if (responseCode != 200) {
                throw new Exception("HTTP响应码: " + responseCode);
            }
            
            // 获取文件大小
            long fileSize = connection.getContentLengthLong();
            
            // 获取内容类型
            String mimeType = connection.getContentType();
            
            // 从URL提取文件名
            String fileName = extractFileNameFromUrl(url);
            
            Map<String, Object> result = new HashMap<>();
            result.put("fileSize", fileSize > 0 ? fileSize : null);
            result.put("fileName", fileName);
            result.put("mimeType", mimeType);
            result.put("responseCode", responseCode);
            result.put("url", url); // 返回处理后的URL
            
            // 对于视频和音频文件，获取真实时长
            if (contentType != null && (contentType == 1 || contentType == 3)) {
                Integer realDuration = getRealMediaDuration(url);
                result.put("duration", realDuration);
            }
            
            return result;
            
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 从URL提取文件名
     */
    private String extractFileNameFromUrl(String url) {
        try {
            String path = new java.net.URL(url).getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            
            // 移除查询参数
            int queryIndex = fileName.indexOf('?');
            if (queryIndex > 0) {
                fileName = fileName.substring(0, queryIndex);
            }
            
            // 移除文件扩展名作为标题
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = fileName.substring(0, dotIndex);
            }
            
            // URL解码
            return java.net.URLDecoder.decode(fileName, "UTF-8");
        } catch (Exception e) {
            return "未知文件";
        }
    }

    /**
     * 获取媒体文件的真实时长
     * 使用FFprobe命令行工具
     */
    private Integer getRealMediaDuration(String url) {
        try {
            // 构建FFprobe命令
            ProcessBuilder pb = new ProcessBuilder(
                "ffprobe", 
                "-v", "quiet", 
                "-show_entries", "format=duration", 
                "-of", "csv=p=0", 
                url
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            
            // 等待进程完成
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && line != null && !line.trim().isEmpty()) {
                try {
                    double duration = Double.parseDouble(line.trim());
                    return (int) Math.round(duration);
                } catch (NumberFormatException e) {
                    System.err.println("无法解析时长: " + line);
                }
            }
            
        } catch (Exception e) {
            System.err.println("FFprobe获取时长失败: " + e.getMessage());
            // 如果FFprobe失败，尝试使用FFmpeg
            return getRealMediaDurationWithFFmpeg(url);
        }
        
        return null;
    }
    
    /**
     * 使用FFmpeg获取媒体时长（备用方案）
     */
    private Integer getRealMediaDurationWithFFmpeg(String url) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", 
                "-i", url, 
                "-f", "null", 
                "-"
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String durationLine = null;
            
            // 查找包含Duration的行
            while ((line = reader.readLine()) != null) {
                if (line.contains("Duration:")) {
                    durationLine = line;
                    break;
                }
            }
            
            process.waitFor();
            
            if (durationLine != null) {
                // 解析Duration: 00:00:10.24, start: 0.000000, bitrate: 612 kb/s
                Pattern pattern = Pattern.compile("Duration: (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{2})");
                Matcher matcher = pattern.matcher(durationLine);
                
                if (matcher.find()) {
                    int hours = Integer.parseInt(matcher.group(1));
                    int minutes = Integer.parseInt(matcher.group(2));
                    int seconds = Integer.parseInt(matcher.group(3));
                    
                    return hours * 3600 + minutes * 60 + seconds;
                }
            }
            
        } catch (Exception e) {
            System.err.println("FFmpeg获取时长失败: " + e.getMessage());
        }
        
        return null;
    }
    private boolean isVideoFile(String contentType, String extension) {
        if (contentType != null && contentType.startsWith("video/")) {
            return true;
        }
        return extension.matches("\\.(mp4|avi|mov|wmv|flv|mkv|m4v|3gp|webm)$");
    }

    /**
     * 检查是否为图片文件
     */
    private boolean isImageFile(String contentType, String extension) {
        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }
        return extension.matches("\\.(jpg|jpeg|png|gif|bmp|webp)$");
    }

    /**
     * 获取视频时长（简化版本）
     * 实际项目中建议使用FFmpeg等专业工具
     */
    private Integer getVideoDuration(File videoFile) {
        try {
            // 这里是一个简化的实现
            // 实际项目中应该使用FFmpeg或其他媒体处理库来获取真实的视频时长
            
            // 临时方案：根据文件大小估算时长（非常不准确，仅用于演示）
            long fileSize = videoFile.length();
            
            // 假设平均码率为1Mbps，这只是一个粗略估算
            // 实际应该使用专业工具解析视频文件
            if (fileSize > 0) {
                // 简单估算：文件大小(MB) / 平均码率(Mbps) * 8
                double fileSizeMB = fileSize / (1024.0 * 1024.0);
                int estimatedDuration = (int) (fileSizeMB / 1.0 * 8 * 60); // 估算秒数
                
                // 限制在合理范围内
                return Math.min(Math.max(estimatedDuration, 10), 7200); // 10秒到2小时
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}