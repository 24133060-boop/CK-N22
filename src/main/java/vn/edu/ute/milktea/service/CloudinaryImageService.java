package vn.edu.ute.milktea.service;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
@Service public class CloudinaryImageService {
    private final String cloudName,apiKey,apiSecret;
    public CloudinaryImageService(@Value("${app.cloudinary.cloud-name:}") String cloudName,@Value("${app.cloudinary.api-key:}") String apiKey,@Value("${app.cloudinary.api-secret:}") String apiSecret){
        this.cloudName=cloudName.trim();
        this.apiKey=apiKey.trim();
        this.apiSecret=apiSecret.trim();
    }
    public String upload(MultipartFile file) throws IOException {
        if(cloudName.isBlank()||apiKey.isBlank()||apiSecret.isBlank())throw new IllegalStateException("Cloudinary chưa được cấu hình");
        if(file==null||file.isEmpty()||file.getSize()>5*1024*1024)throw new IllegalArgumentException("Ảnh phải có dung lượng tối đa 5 MB");
        String mime=file.getContentType();
        if(!("image/jpeg".equals(mime)||"image/png".equals(mime)||"image/webp".equals(mime)))throw new IllegalArgumentException("Chỉ nhận JPEG, PNG hoặc WebP");
        byte[] data=file.getBytes();
        if(!isImage(data))throw new IllegalArgumentException("Tệp tải lên không phải ảnh hợp lệ");
        Cloudinary cloudinary=new Cloudinary(ObjectUtils.asMap("cloud_name",cloudName,"api_key",apiKey,"api_secret",apiSecret,"secure",true));
        Map result=cloudinary.uploader().upload(data,ObjectUtils.asMap("folder","utemilktea/products","resource_type","image"));
        Object url=result.get("secure_url");
        if(!(url instanceof String)||!((String)url).startsWith("https://"))throw new IOException("Không nhận được URL ảnh hợp lệ");
        return (String)url;
    }
    public String uploadReviewMedia(MultipartFile file) throws IOException {
        if(cloudName.isBlank()||apiKey.isBlank()||apiSecret.isBlank())throw new IllegalStateException("Cloudinary chưa được cấu hình");
        if(file==null||file.isEmpty()||file.getSize()>15L*1024*1024)throw new IllegalArgumentException("Tệp đánh giá không vượt quá 15MB");
        String mime=file.getContentType();
        String filename=file.getOriginalFilename()==null?"":file.getOriginalFilename().toLowerCase(java.util.Locale.ROOT);
        boolean video="video/mp4".equalsIgnoreCase(mime) || ("application/octet-stream".equalsIgnoreCase(mime)&&filename.endsWith(".mp4"));
        boolean image="image/jpeg".equalsIgnoreCase(mime)||"image/png".equalsIgnoreCase(mime)||"image/webp".equalsIgnoreCase(mime)
        || ("application/octet-stream".equalsIgnoreCase(mime) && (filename.endsWith(".jpg")||filename.endsWith(".jpeg")||filename.endsWith(".png")||filename.endsWith(".webp")));
        if(!video&&!image)throw new IllegalArgumentException("Chỉ nhận JPG, PNG, WebP hoặc MP4");
        byte[] bytes=file.getBytes();
        if(image&&!isImage(bytes))throw new IllegalArgumentException("Ảnh không hợp lệ");
        if(video && !(bytes.length>12 && bytes[4]=='f' && bytes[5]=='t' && bytes[6]=='y' && bytes[7]=='p'))throw new IllegalArgumentException("Video MP4 không hợp lệ");
        Cloudinary cloud=new Cloudinary(ObjectUtils.asMap("cloud_name",cloudName,"api_key",apiKey,"api_secret",apiSecret,"secure",true));
        Map uploaded=cloud.uploader().upload(bytes,ObjectUtils.asMap("folder","utemilktea/reviews","resource_type",video?"video":"image"));
        Object url=uploaded.get("secure_url");
        if(!(url instanceof String)||!((String)url).startsWith("https://"))throw new IOException("Upload media không trả về URL HTTPS hợp lệ");
        return (String)url;
    }
    private boolean isImage(byte[] b){
        if(b.length<12)return false;
        boolean jpeg=(b[0]&255)==255&&(b[1]&255)==216&&(b[2]&255)==255;
        boolean png=(b[0]&255)==137&&b[1]==80&&b[2]==78&&b[3]==71;
        boolean webp=b[0]==82&&b[1]==73&&b[2]==70&&b[3]==70&&b[8]==87&&b[9]==69&&b[10]==66&&b[11]==80;
        return jpeg||png||webp;
    }
}
