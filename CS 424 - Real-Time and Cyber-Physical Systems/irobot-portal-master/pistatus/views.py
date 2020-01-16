import pytz
from django.core.exceptions import ObjectDoesNotExist
from django.shortcuts import render
from django.utils import timezone
from django.http import HttpResponse, HttpResponseBadRequest, HttpResponseNotAllowed, HttpResponseForbidden
from datetime import datetime, timedelta

# Create your views here.

from pistatus.models import Pi
from ipware import get_client_ip


def index(request):
    active_period = timezone.now() - timedelta(minutes=5)
    active_pis = Pi.objects.filter(last_ping__gt=active_period).order_by('host_name')
    context = {
        'pis': active_pis
    }
    return render(request, 'pistatus/index.html', context)


def split(ifconfig):
    ips = {}
    for paragraph in ifconfig.split('\n\n'):
        print(paragraph)
        interface = paragraph.split(": ")[0]
        lines = paragraph.split("\n")[1].split()
        if lines[0] == 'inet':
            ips[interface] = lines[1]
    return ips


def ping(request):
    serial_key = 'serial'
    host_key = 'host'
    ip_key = 'ip'
    try:
        if request.method == 'POST':
            if not (serial_key in request.POST and host_key in request.POST and ip_key in request.POST):
                raise ValueError("some required key does not present")

            hardware_serial = request.POST[serial_key]
            host_name = request.POST[host_key]
            ifconfig = request.POST[ip_key]

            if not hardware_serial:
                raise ValueError("hardware_serial is empty")

            ips = split(ifconfig)

            wlan_address = ips.get('wlan0', '')
            lan_address = ips.get('eth0', '')
            ip, is_routable = get_client_ip(request)
            time_now = timezone.now()

            remote_address = ip

            try:
                existing_pi = Pi.objects.get(hardware_serial=hardware_serial)
                existing_pi.remote_address = remote_address
                existing_pi.wlan_address = wlan_address
                existing_pi.lan_address = lan_address
                existing_pi.last_ping = time_now
                existing_pi.public_routable = is_routable
                existing_pi.save()
            except ObjectDoesNotExist:
                pi = Pi.objects.create(hardware_serial=hardware_serial,
                                       host_name=host_name,
                                       remote_address=remote_address,
                                       wlan_address=wlan_address,
                                       lan_address=lan_address,
                                       public_routable=is_routable,
                                       last_ping=time_now)
                pi.save()

            print("serial: " + hardware_serial)

            print("host: " + host_name)
        else:

            return HttpResponseNotAllowed("only post request is supported")

    except ValueError as e:
        print("Caught ValueError: {}".format(e))
        return HttpResponseBadRequest("input error: {}\n".format(e))
    except Exception as e:
        print("Caught Exception: {}".format(e))
        print(e)
        return HttpResponseForbidden("expectation failed: {}\n".format(e))
    else:
        return HttpResponse("ping back")
