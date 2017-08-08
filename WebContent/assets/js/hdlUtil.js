var hdlUtil = window.hdlUtil = {
		/**
		 * 简单json数据格式转为标准json数据格式，用于菜单等多层级树结构
		 * @param setting 示例：{idKey:"id",pIdKey:"pId",childKey:'children'}
		 * @param sNodes 简单json数据
		 * @returns {Array}
		 */
		transformToTreeFormat : function(setting, sNodes){
			var i, l, key = setting.idKey, parentKey = setting.pIdKey, childKey = setting.childKey;
			if (!key || key == "" || !sNodes)
				return [];

			if (Object.prototype.toString.apply(sNodes) === "[object Array]") {
				var r = [];
				var tmpMap = [];
				for (i = 0, l = sNodes.length; i < l; i++) {
					tmpMap[sNodes[i][key]] = sNodes[i];
				}
				for (i = 0, l = sNodes.length; i < l; i++) {
					if (tmpMap[sNodes[i][parentKey]]
							&& sNodes[i][key] != sNodes[i][parentKey]) {
						if (!tmpMap[sNodes[i][parentKey]][childKey])
							tmpMap[sNodes[i][parentKey]][childKey] = [];
						tmpMap[sNodes[i][parentKey]][childKey].push(sNodes[i]);
					} else {
						r.push(sNodes[i]);
					}
				}
				return r;
			} else {
				return [ sNodes ];
			}
		}
};




