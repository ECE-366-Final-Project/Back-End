import json

def getCode(callName, declareParams, getAndsetParams, toResponseEntity):
    return  f'package local.API.Responses;\n'\
            f'import org.springframework.http.ResponseEntity;\n\n'\
            f'public class {callName}Reponse extends InternalResponse {{\n\n'\
            f'{declareParams}\n'\
            f'\tpublic {callName}Reponse() {{\n'\
            f'\t\tsuper();\n'\
            f'\t}}\n\n'\
            f'{getAndsetParams}'\
            f'\tpublic ResponseEntity<String> toResponseEntity() {{\n'\
            f'{toResponseEntity}'\
            f'\t}}\n'\
            f'}}'
            
def getAndSet(p_name, p_type):
    return  f'\tpublic {p_type} get{p_name}() {{\n'\
		    f'\t\treturn {p_name};\n'\
	        f'\t}}\n'\
            f'\tpublic void set{p_name}({p_type} {p_name}) {{\n'\
		    f'\t\tthis.{p_name} = {p_name};\n'\
	        f'\t}}\n'

def responseEntity(params):
    response =  '\t\tJSONObject jo = new JSONObject();\n'\
                '\t\tjo.put(\"MESSAGE\", MESSAGE);\n'
    for param in params:
        if param == 'MESSAGE':
            continue
        response += f'\t\tjo.put(\"{param}\", {param});\n'
    response += '\t\treturn new ResponseEntity<String>(jo.toString(), responseCode);\n'
    return response

file = open(r'../Documentation and Testing/docs.json')
data = json.load(file)
file.close()

for callName in data:
    file = open(f'Response{callName}.java', 'w')
    declareParams = ''
    getAndsetParams = ''
    for param_name in data[callName]:
        if param_name == 'MESSAGE':
            continue
        param_type = data[callName][param_name]['Type']
        default_value = data[callName][param_name]['Default']
        if param_type == 'boolean':
            match (default_value):
                case True:
                    default_value = 'true'
                    break
                case False:
                    default_value = 'false'
                    break
                case _:
                    raise ValueError(f'Boolean Type Requires True/False But Got {default_value = }')
        if param_type == 'String':
            default_value = repr(default_value).replace("\'", "\"")
        else:
            default_value = repr(default_value)
        declareParams += f'\tprivate {param_type} {param_name} = {default_value};\n'
        getAndsetParams += getAndSet(param_name, param_type) + '\n'
    toResponseEntity = responseEntity(data[callName])
    file.write(getCode(callName, declareParams, getAndsetParams, toResponseEntity))
    file.close()