package net.cheney.cocktail.message;

public enum Mime {

	TEXT_PLAIN(MediaType.TEXT, "plain", "txt"),
	TEXT_HTML(MediaType.TEXT, "html", "htm", "html"),
	TEXT_RICHTEXT(MediaType.TEXT, "richtext"),
	TEXT_ENRICHED(MediaType.TEXT, "enriched"),
	TEXT_XML(MediaType.TEXT, "xml", "xml"),
	TEXT_CALENDAR(MediaType.TEXT, "calendar"),
	TEXT_CSS(MediaType.TEXT, "css", "css"),
	TEXT_JAVASCRIPT(MediaType.TEXT, "javascript", "js"),
	
	IMAGE_JPEG(MediaType.IMAGE, "jpeg", "jpeg","jpg"),
	IMAGE_GIF(MediaType.IMAGE, "gif", "gif"),
	IMAGE_PNG(MediaType.IMAGE, "png", "png"),
	IMAGE_X_ICON(MediaType.IMAGE, "x-icon", "ico"),
	IMAGE_PSD(MediaType.IMAGE, "psd", "psd"),

	AUDIO_BASIC(MediaType.AUDIO, "basic"),

	VIDEO_MPEG(MediaType.VIDEO, "mpeg", "mpg", "mpeg"),
	VIDEO_X_MS_WMV(MediaType.VIDEO, "x-ms-wmv", "wmv"),
	VIDEO_AVI(MediaType.VIDEO, "avi", "wmv"),
	VIDEO_MSVIDEO(MediaType.VIDEO, "msvideo"),
	VIDEO_X_QUICKTIME(MediaType.VIDEO, "x-quicktime", "mov"),
	
	APPLICATION_OCTET_STREAM(MediaType.APPLICATION, "octet-stream"),
	APPLICATION_POSTSCRIPT(MediaType.APPLICATION, "postscript", "ps"),
	APPLICATION_PDF(MediaType.APPLICATION, "pdf", "pdf"),
	APPLICATION_MSPOWERPOINT(MediaType.APPLICATION, "mspowerpoint", "ppt"),
	APPLICATION_MS_POWERPOINT(MediaType.APPLICATION, "ms-powerpoint", "ppt"),
	APPLICATION_MSPOWERPNT(MediaType.APPLICATION, "mspowerpnt", "ppt"),
	APPLICATION_HTTP(MediaType.APPLICATION, "http"),
	APPLICATION_XHTML_XML(MediaType.APPLICATION, "xhtml+xml"),
	APPLICATION_XML(MediaType.APPLICATION, "xml"),
	APPLICATION_RSS_XML(MediaType.APPLICATION, "rss-xml"),
	APPLICATION_ATOM_XML(MediaType.APPLICATION, "atom-xml"),
	APPLICATION_X_BITTORRENT(MediaType.APPLICATION, "x-bittorrent", "torrent"),
	APPLICATION_X_GZIP(MediaType.APPLICATION, "x-gzip", "gz"),
	APPLICATION_ZIP(MediaType.APPLICATION, "zip", "zip"),
	APPLICATION_JAVA_ARCHIVE(MediaType.APPLICATION, "java-archive", "jar"),
	APPLICATION_MSWORD(MediaType.APPLICATION, "msword", "doc"),
	APPLICATION_TAR(MediaType.APPLICATION, "tar", "tar"),

	MULTIPART_MIXED(MediaType.MULTIPART, "mixed"),
	MULTIPART_ALTERNATIVE(MediaType.MULTIPART, "alternative"),
	MULTIPART_PARALLEL(MediaType.MULTIPART, "parallel"),
	MULTIPART_DIGEST(MediaType.MULTIPART, "digest"),
	MULTIPATH_BYTERANGES(MediaType.MULTIPART, "byteranges"),

	MESSAGE_RFC822(MediaType.MESSAGE, "rfc-822"),
	MESSAGE_PARTIAL(MediaType.MESSAGE, "partial"),
	MESSAGE_EXTERNAL(MediaType.MESSAGE, "external"),
	MESSAGE_HTTP(MediaType.MESSAGE, "http"),
	APPLICATION_RDF_XML(MediaType.APPLICATION, "rdf+xml"),
	
	ANY_ANY(MediaType.ANY, "*");
	
	public enum MediaType {
	
		TEXT, IMAGE, AUDIO, VIDEO, APPLICATION, MULTIPART, MESSAGE, ANY;
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
	
		public static Mime.MediaType parse(String mediaType) {
			return valueOf(mediaType.toUpperCase());
		}
	}

	private final Mime.MediaType mediaType;
	private final String subType;
	
	private Mime(Mime.MediaType mediaType, String subType, String... extensions) {
		this.mediaType = mediaType;
		this.subType = subType;
	}
	
	public String toString() {
		return String.format("%s/%s", mediaType.toString(), subType);
	}

	public boolean isCompatible(Mime c) {
		// TODO Auto-generated method stub
		return false;
	}
}