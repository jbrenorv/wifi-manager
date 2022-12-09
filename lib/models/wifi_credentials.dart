import 'dart:convert';

enum WifiSecurityType { none, wpa2, wpa3 }

class WifiCredentials {
  final String ssid;
  final String? password;
  final WifiSecurityType wifiSecurityType;

  WifiCredentials({
    required this.ssid,
    this.password,
    this.wifiSecurityType = WifiSecurityType.none,
  });

  bool get hasSecurity =>
      (password != null && wifiSecurityType != WifiSecurityType.none);

  WifiCredentials copyWith({
    String? ssid,
    String? password,
    WifiSecurityType? wifiSecurityType,
  }) {
    return WifiCredentials(
      ssid: ssid ?? this.ssid,
      password: password ?? this.password,
      wifiSecurityType: wifiSecurityType ?? this.wifiSecurityType,
    );
  }

  Map<String, dynamic> toMap() {
    final result = <String, dynamic>{};

    result.addAll({'ssid': ssid});
    result.addAll({'password': password});
    result.addAll({'wifiSecurityType': wifiSecurityType.index});

    return result;
  }

  factory WifiCredentials.fromMap(Map<String, dynamic> map) {
    return WifiCredentials(
      ssid: map['ssid'] ?? '',
      password: map['password'],
      wifiSecurityType: WifiSecurityType.values[map['wifiSecurityType']],
    );
  }

  String toJson() => json.encode(toMap());

  factory WifiCredentials.fromJson(String source) =>
      WifiCredentials.fromMap(json.decode(source));

  @override
  String toString() =>
      'WifiCredentials(ssid: $ssid, password: $password, wifiSecurityType: $wifiSecurityType)';

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is WifiCredentials &&
        other.ssid == ssid &&
        other.password == password &&
        other.wifiSecurityType == wifiSecurityType;
  }

  @override
  int get hashCode =>
      ssid.hashCode ^ password.hashCode ^ wifiSecurityType.hashCode;
}
