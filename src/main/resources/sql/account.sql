#sql("getAccountByName")
	select *
	from account
	#set(flag=0)
	#if(name)
		#(flag==0?"where":"and") accountName=#para(name)
		#set(flag=1)
	#end
#end