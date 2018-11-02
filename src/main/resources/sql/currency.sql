#sql("paginate")
	select *
	from currency
	#set(flag=0)
	#if(qo.name)
		#(flag==0?"where":"and") name like concat("%",#para(qo.name),"%")
		#set(flag=1)
	#end
	#if(qo.code)
		#(flag==0?"where":"and") code=#para(qo.code)
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