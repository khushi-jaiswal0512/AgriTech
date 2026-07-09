import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../../domain/entities/user.dart';
import '../../domain/repositories/auth_repository.dart';
import '../datasources/auth_remote_datasource.dart';

class AuthRepositoryImpl implements AuthRepository {
  final AuthRemoteDataSource remoteDataSource;
  final FlutterSecureStorage secureStorage;

  AuthRepositoryImpl({
    required this.remoteDataSource,
    required this.secureStorage,
  });

  @override
  Future<User> login(String email, String password) async {
    final result = await remoteDataSource.login(email, password);
    final String token = result['token'];
    final User user = result['user'];

    await secureStorage.write(key: 'jwt_token', value: token);
    await secureStorage.write(key: 'user_role', value: user.role);
    await secureStorage.write(key: 'user_id', value: user.id.toString());

    return user;
  }

  @override
  Future<void> logout() async {
    await secureStorage.deleteAll();
  }
}
