/**
 * 地图工具类
 * 封装高德地图相关功能
 */

// 地图API加载状态
let mapApiLoaded = false;
let mapApiLoading = false;

/**
 * 动态加载高德地图API
 * @returns {Promise} 返回Promise，加载完成后resolve
 */
export function loadAmapApi() {
    return new Promise((resolve, reject) => {
        // 如果已经加载，直接resolve
        if (window.AMap) {
            mapApiLoaded = true;
            resolve();
            return;
        }
        
        // 如果正在加载，等待加载完成
        if (mapApiLoading) {
            const checkInterval = setInterval(() => {
                if (window.AMap) {
                    clearInterval(checkInterval);
                    mapApiLoaded = true;
                    resolve();
                }
            }, 100);
            return;
        }
        
        // 开始加载
        mapApiLoading = true;
        
        // 从环境变量获取API Key，如果没有则使用默认值
        const apiKey = process.env.VUE_APP_AMAP_KEY || 'YOUR_AMAP_KEY';
        
        console.log('读取到的API Key:', apiKey ? apiKey.substring(0, 8) + '...' : '未配置');
        
        if (apiKey === 'YOUR_AMAP_KEY' || !apiKey) {
            console.warn('高德地图API Key未配置，请在.env文件中设置VUE_APP_AMAP_KEY');
            console.warn('当前环境变量值:', process.env.VUE_APP_AMAP_KEY);
            reject(new Error('高德地图API Key未配置，请在frontend目录下创建.env文件并设置VUE_APP_AMAP_KEY'));
            return;
        }
        
        // 创建script标签
        const script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = `https://webapi.amap.com/maps?v=2.0&key=${apiKey}&plugin=AMap.Geocoder,AMap.Driving,AMap.PlaceSearch`;
        script.async = true;
        
        script.onload = () => {
            mapApiLoaded = true;
            mapApiLoading = false;
            console.log('高德地图API加载成功');
            resolve();
        };
        
        script.onerror = () => {
            mapApiLoading = false;
            console.error('高德地图API加载失败');
            reject(new Error('高德地图API加载失败，请检查网络连接和API Key'));
        };
        
        // 添加到head
        document.head.appendChild(script);
    });
}

/**
 * 初始化地图
 * @param {String} containerId 地图容器ID
 * @param {Object} options 地图配置选项
 * @returns {Promise<AMap.Map>} 返回Promise，resolve包含地图实例
 */
export async function initMap(containerId, options = {}) {
    // 确保API已加载
    await loadAmapApi();
    
    if (!window.AMap) {
        throw new Error('高德地图API未加载，请检查API Key配置');
    }
    
    const defaultOptions = {
        zoom: 13,
        center: [116.397428, 39.90923], // 默认中心点（北京）
        viewMode: '3D',
        ...options
    };
    
    return new AMap.Map(containerId, defaultOptions);
}

/**
 * 地址解析（Geocoding）
 * @param {String} address 地址文本
 * @returns {Promise} 返回Promise，resolve包含{lng, lat, address}
 */
export function geocodeAddress(address) {
    return new Promise((resolve, reject) => {
        if (!window.AMap) {
            reject(new Error('高德地图API未加载'));
            return;
        }
        
        if (!address || !address.trim()) {
            reject(new Error('地址不能为空'));
            return;
        }
        
        // 清理地址，去除多余空格
        const cleanAddress = address.trim();
        
        // 如果地址不包含城市信息，尝试添加常见城市（可以根据实际情况调整）
        let searchAddress = cleanAddress;
        
        // 如果地址看起来不完整（比如只有学校名称），尝试添加"大学"或"学院"等关键词
        // 或者使用POI搜索
        console.log('开始解析地址:', cleanAddress);

        // 如果地址过短或不包含城市/区/省等关键词，优先使用POI搜索（对学校名、宿舍等更友好）
        const shortAddressPattern = /^.{0,6}$|(?:大学|学院|宿舍|校区|教室|寝室)/i;
        const containsCityPattern = /(市|省|区|县|县级|自治州|市辖区)/i;

        const tryPlaceSearchFirst = shortAddressPattern.test(cleanAddress) && !containsCityPattern.test(cleanAddress);

        const geocoder = new AMap.Geocoder({
            city: '全国', // 全国范围搜索
            timeout: 10000 // 10秒超时
        });

        // helper to handle geocoder callback
        const handleGeocoderResult = (status, result) => {
            console.log('地址解析结果:', status, result);
            if (status === 'complete' && result.geocodes && result.geocodes.length > 0) {
                const location = result.geocodes[0].location;
                const formattedAddress = result.geocodes[0].formattedAddress || cleanAddress;
                console.log('地址解析成功:', {
                    original: cleanAddress,
                    formatted: formattedAddress,
                    location: [location.lng, location.lat]
                });
                resolve({
                    lng: location.lng,
                    lat: location.lat,
                    address: cleanAddress,
                    formattedAddress: formattedAddress
                });
            } else {
                console.warn('地理编码失败，尝试POI搜索:', cleanAddress);
                searchByPOI(cleanAddress).then(resolve).catch((err) => {
                    console.error('地址解析失败:', cleanAddress, '状态:', status, 'err:', err && err.message);
                    reject(new Error(`地址解析失败: "${cleanAddress}"。请确保地址包含城市和详细位置，例如："北京市清华大学"`));
                });
            }
        };

        // 优先尝试后端代理（可以绕过前端 Referer 白名单问题）
        const proxyUrl = `/api/geocode?address=${encodeURIComponent(cleanAddress)}`;
        console.log('先尝试后端代理解析地址:', proxyUrl);
        fetch(proxyUrl, { method: 'GET' })
            .then(res => res.json())
            .then(data => {
                try {
                    console.log('后端代理返回:', data);
                    if (data && data.status_code === 1 && data.data) {
                        const d = data.data;
                        if (d.lng && d.lat) {
                            resolve({
                                lng: Number(d.lng),
                                lat: Number(d.lat),
                                address: cleanAddress,
                                formattedAddress: d.formattedAddress || cleanAddress
                            });
                            return;
                        }
                    }
                } catch (e) {
                    console.warn('解析后端代理响应异常，继续使用前端SDK解析', e);
                }
                // 如果代理失败，再使用前端策略（先POI或geocoder）
                if (tryPlaceSearchFirst) {
                    console.log('代理未命中，优先尝试POI搜索:', cleanAddress);
                    searchByPOI(cleanAddress).then(resolve).catch((err) => {
                        console.warn('POI搜索未命中，回退地理编码:', err && err.message);
                        geocoder.getLocation(cleanAddress, handleGeocoderResult);
                    });
                } else {
                    geocoder.getLocation(cleanAddress, handleGeocoderResult);
                }
            })
            .catch(err => {
                console.warn('后端代理调用失败，使用客户端解析:', err);
                if (tryPlaceSearchFirst) {
                    searchByPOI(cleanAddress).then(resolve).catch(() => geocoder.getLocation(cleanAddress, handleGeocoderResult));
                } else {
                    geocoder.getLocation(cleanAddress, handleGeocoderResult);
                }
            });
    });
}

/**
 * 使用POI搜索地址（当地理编码失败时使用）
 * @param {String} keyword 搜索关键词
 * @returns {Promise} 返回Promise，resolve包含{lng, lat, address}
 */
function searchByPOI(keyword) {
    return new Promise((resolve, reject) => {
        if (!window.AMap) {
            reject(new Error('高德地图API未加载'));
            return;
        }
        
        // 尝试提取学校名称（去除宿舍等后缀）
        let searchKeyword = keyword;
        
        // 如果包含"宿舍"、"校区"等关键词，尝试提取学校名称部分
        const schoolPatterns = [
            /^(.+?)(?:校区|宿舍|东区|西区|南区|北区|.*?宿舍)/,  // 匹配"学校名称+校区/宿舍"
            /^(.+?大学)/,  // 匹配"XX大学"
            /^(.+?学院)/,  // 匹配"XX学院"
            /^(.+?学校)/,  // 匹配"XX学校"
        ];
        
        for (const pattern of schoolPatterns) {
            const match = keyword.match(pattern);
            if (match && match[1]) {
                searchKeyword = match[1].trim();
                console.log('提取学校名称:', searchKeyword, '从:', keyword);
                break;
            }
        }
        
        const placeSearch = new AMap.PlaceSearch({
            city: '全国',
            citylimit: false,
            type: '高等院校|科教文化服务' // 优先搜索学校
        });
        
        // 先尝试搜索提取的学校名称
        placeSearch.search(searchKeyword, (status, result) => {
            if (status === 'complete' && result.poiList && result.poiList.pois && result.poiList.pois.length > 0) {
                const poi = result.poiList.pois[0];
                const location = poi.location;
                
                console.log('POI搜索成功:', {
                    keyword: keyword,
                    searchKeyword: searchKeyword,
                    name: poi.name,
                    address: poi.address,
                    location: [location.lng, location.lat]
                });
                
                resolve({
                    lng: location.lng,
                    lat: location.lat,
                    address: keyword,
                    formattedAddress: poi.address || poi.name
                });
            } else {
                // 如果提取的学校名称搜索失败，尝试用原始关键词搜索
                if (searchKeyword !== keyword) {
                    console.log('学校名称搜索失败，尝试原始关键词:', keyword);
                    placeSearch.search(keyword, (status2, result2) => {
                        if (status2 === 'complete' && result2.poiList && result2.poiList.pois && result2.poiList.pois.length > 0) {
                            const poi = result2.poiList.pois[0];
                            const location = poi.location;
                            
                            resolve({
                                lng: location.lng,
                                lat: location.lat,
                                address: keyword,
                                formattedAddress: poi.address || poi.name
                            });
                        } else {
                            reject(new Error('POI搜索失败: ' + keyword));
                        }
                    });
                } else {
                    reject(new Error('POI搜索失败: ' + keyword));
                }
            }
        });
    });
}

/**
 * 添加地图标记
 * @param {AMap.Map} map 地图实例
 * @param {Object} location 位置对象 {lng, lat, address}
 * @param {String} title 标记标题
 * @param {String} type 标记类型 'seller' | 'buyer'
 */
export function addMarker(map, location, title, type = 'default') {
    if (!map || !location) {
        console.warn('addMarker: 参数无效', { map, location });
        return null;
    }
    
    if (!location.lng || !location.lat) {
        console.error('addMarker: 位置坐标无效', location);
        return null;
    }
    
    console.log('添加标记:', { title, type, location: [location.lng, location.lat] });
    
    const iconMap = {
        seller: 'https://webapi.amap.com/theme/v1.3/markers/n/mid_red.png', // 红色（卖家）
        buyer: 'https://webapi.amap.com/theme/v1.3/markers/n/mid_blue.png', // 蓝色（买家）
        default: 'https://webapi.amap.com/theme/v1.3/markers/n/mid_green.png' // 绿色（默认）
    };
    
    const icon = iconMap[type] || iconMap.default;
    
    const marker = new AMap.Marker({
        position: [location.lng, location.lat],
        title: title,
        icon: new AMap.Icon({
            image: icon,
            size: new AMap.Size(32, 32),
            imageSize: new AMap.Size(32, 32)
        })
    });
    
    // 添加信息窗口
    const infoWindow = new AMap.InfoWindow({
        content: `<div style="padding: 5px; min-width: 150px;">
            <strong style="color: #409EFF;">${title}</strong><br/>
            <span style="font-size: 12px; color: #666;">${location.address || location.formattedAddress || ''}</span>
        </div>`
    });
    
    marker.on('click', () => {
        infoWindow.open(map, marker.getPosition());
    });
    
    map.add(marker);
    console.log('标记已添加到地图:', marker, '位置:', [location.lng, location.lat]);
    
    return marker;
}

/**
 * 计算两点间距离
 * @param {Object} location1 位置1 {lng, lat}
 * @param {Object} location2 位置2 {lng, lat}
 * @returns {Number} 距离（米）
 */
export function calculateDistance(location1, location2) {
    if (!window.AMap || !location1 || !location2) {
        return null;
    }
    
    return AMap.GeometryUtil.distance(
        [location1.lng, location1.lat],
        [location2.lng, location2.lat]
    );
}

/**
 * 格式化距离文本
 * @param {Number} distance 距离（米）
 * @returns {String} 格式化后的距离文本
 */
export function formatDistance(distance) {
    if (distance === null || distance === undefined) {
        return '计算中...';
    }
    
    if (distance < 1000) {
        return `约 ${Math.round(distance)} 米`;
    } else {
        return `约 ${(distance / 1000).toFixed(2)} 公里`;
    }
}

/**
 * 更新地图视野，包含所有标记点
 * @param {AMap.Map} map 地图实例
 * @param {Array} locations 位置数组 [{lng, lat}, ...]
 */
export function fitMapView(map, locations) {
    if (!map || !locations || locations.length === 0) {
        console.warn('fitMapView: 参数无效', { map, locations });
        return;
    }
    
    // 过滤无效坐标
    const points = locations.map(loc => {
        if (!loc || typeof loc.lng !== 'number' || typeof loc.lat !== 'number') {
            console.error('fitMapView: 位置坐标无效', loc);
            return null;
        }
        return [loc.lng, loc.lat];
    }).filter(p => p !== null);
    
    if (points.length === 0) {
        console.error('fitMapView: 没有有效的坐标点');
        return;
    }
    
    console.log('调整地图视野，包含点:', points);
    
    try {
        if (points.length === 1) {
            // 只有一个点，直接设置中心点和缩放
            map.setCenter(points[0]);
            map.setZoom(15);
            console.log('地图视野调整成功（单点）:', points[0]);
        } else {
            // 多个点，使用setFitView
            // 高德地图的setFitView需要传入坐标数组，而不是Marker数组
            map.setFitView(points, false, [50, 50, 50, 50]);
            console.log('地图视野调整成功（多点）:', points);
        }
    } catch (e) {
        console.error('调整地图视野失败:', e);
        // 如果setFitView失败，至少设置中心点
        if (points.length > 0) {
            map.setCenter(points[0]);
            map.setZoom(15);
            console.log('使用备用方法设置地图中心:', points[0]);
        }
    }
}

/**
 * 打开导航
 * @param {Object} location 目标位置 {lng, lat, address}
 * @param {String} mode 导航模式 'car' | 'walk' | 'bus'
 */
export function openNavigation(location, mode = 'car') {
    if (!location || !location.lng || !location.lat) {
        throw new Error('位置信息不完整');
    }
    
    const toName = location.address || location.formattedAddress || '目的地';
    const url = `https://uri.amap.com/navigation?to=${location.lng},${location.lat}&toName=${encodeURIComponent(toName)}&mode=${mode}&policy=1&src=mypage&callnative=1`;
    window.open(url, '_blank');
}

/**
 * 绘制驾驶路线（从start到end）
 * @param {AMap.Map} map 地图实例
 * @param {Object} start {lng, lat}
 * @param {Object} end {lng, lat}
 * @returns {Promise} resolve(routeInfo)
 */
export function drawDrivingRoute(map, start, end) {
    return new Promise((resolve, reject) => {
        if (!window.AMap) {
            reject(new Error('高德地图API未加载'));
            return;
        }
        if (!map || !start || !end) {
            reject(new Error('drawDrivingRoute: 参数不足'));
            return;
        }

        try {
            // 清理已有路线
            clearRoute(map);

            const driving = new AMap.Driving({
                // 使用默认策略
            });

            const origin = [start.lng, start.lat];
            const destination = [end.lng, end.lat];

            driving.search(origin, destination, (status, result) => {
                if (status !== 'complete' || !result || !result.routes || result.routes.length === 0) {
                    reject(new Error('路线搜索失败'));
                    return;
                }

                const route = result.routes[0];
                // 合并所有路径点
                const path = [];
                if (route && route.steps) {
                    route.steps.forEach(step => {
                        if (step.path && step.path.length > 0) {
                            step.path.forEach(p => path.push(p));
                        }
                    });
                }

                // 创建折线
                const polyline = new AMap.Polyline({
                    path: path,
                    strokeColor: '#3366FF',
                    strokeWeight: 5,
                    strokeOpacity: 0.8,
                    showDir: true
                });

                // 起终点标记
                const startMarker = new AMap.Marker({
                    position: origin,
                    title: '起点',
                    icon: new AMap.Icon({
                        image: 'https://webapi.amap.com/theme/v1.3/markers/n/start.png',
                        size: new AMap.Size(24, 34),
                        imageSize: new AMap.Size(24, 34)
                    })
                });
                const endMarker = new AMap.Marker({
                    position: destination,
                    title: '终点',
                    icon: new AMap.Icon({
                        image: 'https://webapi.amap.com/theme/v1.3/markers/n/end.png',
                        size: new AMap.Size(24, 34),
                        imageSize: new AMap.Size(24, 34)
                    })
                });

                // 将覆盖物添加到地图并保存引用以便清理
                map.add(polyline);
                map.add(startMarker);
                map.add(endMarker);

                if (!map.__routeOverlays) map.__routeOverlays = [];
                map.__routeOverlays.push(polyline, startMarker, endMarker);

                // 适配视野
                try {
                    map.setFitView([polyline], false, [50, 50, 50, 50]);
                } catch (e) {
                    // fallback: fit bounds by points
                    try {
                        map.setFitView();
                    } catch (e2) {}
                }

                resolve({
                    distance: route.distance || null,
                    duration: route.duration || null,
                    path
                });
            });
        } catch (e) {
            reject(e);
        }
    });
}

/**
 * 清除当前地图上的路线覆盖物（如果存在）
 * @param {AMap.Map} map
 */
export function clearRoute(map) {
    if (!map) return;
    if (map.__routeOverlays && Array.isArray(map.__routeOverlays)) {
        try {
            map.__routeOverlays.forEach(ov => {
                try {
                    map.remove(ov);
                } catch (e) {}
            });
        } catch (e) {}
        map.__routeOverlays = [];
    }
}

