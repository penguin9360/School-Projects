from django.db import models

# Create your models here.
class Pi(models.Model):
    hardware_serial = models.CharField(max_length=64, primary_key=True)
    host_name = models.CharField(max_length=64)
    remote_address = models.CharField(max_length=16)
    public_routable = models.BooleanField(default=False)
    wlan_address = models.CharField(max_length=16, null=True)
    lan_address = models.CharField(max_length=16, null=True)
    last_ping = models.DateTimeField('last ping received')