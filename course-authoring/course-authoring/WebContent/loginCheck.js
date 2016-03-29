		var usrName = null;
		location.search.substr(1).split("&").forEach(function (item){
			tmp = item.split("=");
			if(tmp[0] === "username")
				usrName = decodeURIComponent(tmp[1]);
		});
		var hashCode = $.cookie(usrName);
		if((usrName != null) && (hashCode != undefined)){
			$.get(centralUrl + "centralCheck", {username : usrName, hashVal : hashCode}).done(function(data){
				if(data.status === "1"){
					CA.actions.init(usrName);
				}else{
					alert('Please use the central portal to login.');
					window.location.href = centralUrl;
				}
			});
		}else{
			alert('Please use the central portal to login.');
			window.location.href = centralUrl;
		}