#sql("paginate")
	select *
	from currency
	#set(flag=0)
	#if(qo.keyword)
		#(flag==0?"where":"and") id like concat('%',#para(qo.keyword),'%')
		#set(flag=1)
	#end
#end

#sql("findByCode")
	select *
	from currency
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") code = #para(code)
		#set(flag=1)
	#end
#end

#sql("listAll")
	select * from currency
#end