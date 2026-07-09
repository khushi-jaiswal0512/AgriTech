import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:get_it/get_it.dart';

import '../core/network/api_client.dart';
import '../features/auth/data/datasources/auth_remote_datasource.dart';
import '../features/auth/data/repositories/auth_repository_impl.dart';
import '../features/auth/domain/repositories/auth_repository.dart';
import '../features/auth/domain/usecases/login_usecase.dart';
import '../features/auth/presentation/bloc/auth_bloc.dart';

final getIt = GetIt.instance;

void setupDI() {
  // 1. Core
  getIt.registerLazySingleton(() => Dio());
  getIt.registerLazySingleton(() => const FlutterSecureStorage());
  getIt.registerLazySingleton(() => ApiClient(dio: getIt(), secureStorage: getIt()));

  // 2. Data Sources
  getIt.registerLazySingleton(() => AuthRemoteDataSource(apiClient: getIt()));

  // 3. Repositories
  getIt.registerLazySingleton<AuthRepository>(
    () => AuthRepositoryImpl(remoteDataSource: getIt(), secureStorage: getIt()),
  );

  // 4. Use Cases
  getIt.registerLazySingleton(() => LoginUseCase(repository: getIt()));

  // 5. BLoCs
  getIt.registerFactory(() => AuthBloc(loginUseCase: getIt()));
}
