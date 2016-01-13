from django.shortcuts import render
from django.shortcuts import render_to_response
from elasticsearch import Elasticsearch
# Create your views here.

def home(request):
    return render_to_response('index.html')


def search(request):
    query = request.GET.get('query')
    es = Elasticsearch()
    res = es.search(index='baike',
                    doc_type='baike', body={"query" : {
          "query_string" : {
               "default_field" : "content",
               "query" : query
          }
       }})
    result = []
    for source in res['hits']['hits']:
        result.append(source['_source']['content'])

    return render_to_response('result.html', {'query': query, 'took': res['took'], 'total': res['hits']['total'],
                                                  'result': result})

