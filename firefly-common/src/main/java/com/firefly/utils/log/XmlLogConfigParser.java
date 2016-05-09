package com.firefly.utils.log;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.firefly.utils.ConvertUtils;
import com.firefly.utils.dom.DefaultDom;
import com.firefly.utils.dom.Dom;
import com.firefly.utils.function.Action1;
import com.firefly.utils.log.file.FileLog;

public class XmlLogConfigParser extends AbstractLogConfigParser {

	@Override
	public boolean parse(Action1<FileLog> action) {
		Dom dom = new DefaultDom();
		Document doc = dom.getDocument(DEFAULT_XML_CONFIG_FILE_NAME);
		Element root = dom.getRoot(doc);
		List<Element> loggerList = dom.elements(root, "logger");
		if (loggerList == null || loggerList.isEmpty()) {
			return false;
		} else {
			for (Element e : loggerList) {
				String name = dom.getTextValueByTagName(e, "name", DEFAULT_LOG_NAME);
				String level = dom.getTextValueByTagName(e, "level", DEFAULT_LOG_LEVEL);
				String path = dom.getTextValueByTagName(e, "path", DEFAULT_LOG_DIRECTORY.getAbsolutePath());
				boolean consoleEnabled = ConvertUtils.convert(dom.getTextValueByTagName(e, "enable-console"),
						DEFAULT_CONSOLE_ENABLED);
				int maxFileSize = ConvertUtils.convert(dom.getTextValueByTagName(e, "max-file-size"),
						DEFAULT_MAX_FILE_SIZE);
				action.call(createLog(name, level, path, consoleEnabled, maxFileSize));
			}
		}
		return true;
	}

}
